package com.bitat.repository.groupChat

import androidx.collection.MutableLongObjectMap
import androidx.compose.runtime.currentCompositionLocalContext
import com.bitat.MainCo
import com.bitat.ext.flowbus.postEventValue
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.KeySecret
import com.bitat.repository.groupChat.UdpClient.HB_INTERVAL
import com.bitat.repository.groupChat.UdpClient.MAX_INACTIVE_MS
import com.bitat.repository.singleChat.MsgDto.ChatMsg
import com.bitat.repository.store.TokenStore
import com.bitat.repository.store.UserStore
import com.bitat.utils.TimeUtils
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

object UdpClient {

    const val HB_INTERVAL = 15 * 1000L
    const val MAX_INACTIVE_MS = 3 * 1000L

    private val ownerDict = MutableLongObjectMap<UdpOwner>()

    private var conn: DatagramChannel? = null

    private var timing: Job? = null

    private var reading: Job? = null

    fun start() {
        close()
        timing = MainCo.launch(IO) {
            while (!KeySecret.isValid()) delay(1000)
            conn = DatagramChannel.open().apply {
                configureBlocking(false)
            }
            if (authAll()) {
                read()
                delay(1000)
                while (isReady()) {
                    val nowTime = TimeUtils.getNow()
                    ownerDict.forEachValue {
                        val inactiveMs = it.inactiveMs(nowTime)
                        if (inactiveMs > 0) {
                            write(it, it.pingBytes())
                        }
                    }
                    delay(1000)
                }
            }
        }
    }

    private suspend fun auth(owner: UdpOwner) = authCommon { write(owner, it) }

    private suspend fun authAll() = authCommon(::writeAll)

    private suspend fun authCommon(writeFn: (ByteArray) -> Unit): Boolean {
        val token = TokenStore.fetchToken()
        if (token != null) {
            while (!KeySecret.isValid()) delay(1000)
            val body = token.toByteArray()
            val key = KeySecret.currentKey()
            val head = UdpMsgHead(key, UdpMsgEvent.AUTH).toBytes()
            val data = KeySecret.encryptByKey(key, body, head)
            if (data != null) {
                writeFn(data)
                return true
            } else CuLog.error(CuTag.GroupChat, "Bad encrypt")
        } else CuLog.error(CuTag.GroupChat, "Bad token")
        return false
    }

    private fun read() {
        reading = MainCo.launch(IO) {
            val byteBuf = ByteBuffer.allocate(1024)
            while (isReady()) {
                val addr = conn?.receive(byteBuf)
                if (addr != null) {
                    byteBuf.flip()
                    val remaining = byteBuf.remaining()
                    val newBuf = ByteArray(remaining)
                    if (remaining > 0 && addr is InetSocketAddress) {
                        byteBuf.get(newBuf, 0, remaining)

                    }
                }
                //37ms的延迟，监听线程得到短暂释放
                else delay(37)
            }
        }
    }

    private fun genMsg(event: Short, body: ByteArray): ByteArray? {
        if (KeySecret.isValid()) {
            val key = KeySecret.currentKey()
            val head = UdpMsgHead(key, event)
            return KeySecret.encryptByKey(key, body, head.toBytes())
        }
        return null
    }

    private fun chat(toId: Long, kind: Int, data: ByteArray) {
        MainCo.launch(IO) {
            val selfId = UserStore.userInfo.id
            val body = ChatMsg.newBuilder().also {
                it.toId = toId
                it.fromId = selfId
                it.toRouter = 0
                it.fromRouter = 0
                it.time = TimeUtils.getNow()
                it.kind = kind
                it.data = ByteString.copyFrom(data)
            }.build()
            val msg = genMsg(UdpMsgEvent.CHAT, body.toByteArray())
            val owner = ownerDict[toId]
            if (msg != null && owner != null) write(owner, msg)
            else CuLog.error(CuTag.SingleChat, "Bad gen msg")
        }
    }

    private fun write(owner: UdpOwner, bytes: ByteArray) {
        val byteBuf = ByteBuffer.wrap(bytes)
        conn?.send(byteBuf, owner.addr)
        byteBuf.clear()
    }

    private fun writeAll(bytes: ByteArray) {
        val byteBuf = ByteBuffer.wrap(bytes)
        ownerDict.forEachValue {
            conn?.send(byteBuf, it.addr)
        }
        byteBuf.clear()
    }

    private fun isReady() = conn?.isOpen ?: false

    fun close() {
        timing?.cancel()
        timing = null
        reading?.cancel()
        reading = null
        conn?.close()
        conn = null
        ownerDict.clear()
    }
}

class UdpOwner(var addr: InetSocketAddress, var verify: String, var name: String) {
    var readTime: Long = 0
    var banChatTime: Long = 0
    fun inactiveMs(nowTime: Long) = nowTime - readTime - HB_INTERVAL
    fun isOnline(nowTime: Long) = inactiveMs(nowTime) < MAX_INACTIVE_MS

    fun pingBytes(): ByteArray {
        return byteArrayOf()
    }
}

class UdpMsgHead(val secret: Short, val event: Short) {

    companion object {
        const val SIZE = 2
        fun fromBytes(bytes: ByteArray): UdpMsgHead {
            return UdpMsgHead(bytes[0].toUByte().toShort(), bytes[1].toShort())
        }
    }

    fun toBytes(): ByteArray {
        val bytes = ByteArray(SIZE)
        bytes[0] = secret.toByte()
        bytes[1] = event.toByte()
        return bytes
    }

}

object UdpMsgEvent {
    const val AUTH = 1.toShort()
    const val AUTH_REC = (-AUTH).toShort()
    const val CHAT = 2.toShort()
}


