package com.bitat.repository.groupChat

import androidx.collection.MutableLongObjectMap
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.KeySecret
import com.bitat.repository.groupChat.UdpClient.HB_INTERVAL
import com.bitat.repository.groupChat.UdpClient.MAX_INACTIVE_MS
import com.bitat.repository.singleChat.TcpMsgEvent
import com.bitat.repository.store.TokenStore
import com.bitat.utils.TimeUtils
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
        clear()
        MainCo.launch(IO) {
            while (!KeySecret.isValid()) delay(1000)
            conn = DatagramChannel.open().apply {
                configureBlocking(false)
            }
            timing = MainCo.launch(IO) {
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
    }

    private suspend fun auth(owner: UdpOwner): Boolean {
        val token = TokenStore.fetchToken()
        if (token != null) {
            while (!KeySecret.isValid()) delay(1000)
            val body = token.toByteArray()
            val key = KeySecret.currentKey()
            val head = UdpMsgHead(key, TcpMsgEvent.AUTH).toBytes()
            val data = KeySecret.encryptByKey(key, body, head)
            if (data != null) {
                write(owner, data)
            }
            return true
        }
        return false
    }

    private suspend fun authAll(): Boolean {
        val token = TokenStore.fetchToken()
        if (token != null) {
            while (!KeySecret.isValid()) delay(1000)
            val body = token.toByteArray()
            val key = KeySecret.currentKey()
            val head = UdpMsgHead(key, TcpMsgEvent.AUTH).toBytes()
            val data = KeySecret.encryptByKey(key, body, head)
            if (data != null) {
                val byteBuf = ByteBuffer.wrap(data)
                ownerDict.forEachValue {
                    conn?.send(byteBuf, it.addr)
                }
                byteBuf.clear()
            }
            return true
        }
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
                    if (remaining > 0) byteBuf.get(newBuf, 0, remaining)
                }
                //37ms的延迟，监听线程得到短暂释放
                else delay(37)
            }
        }
    }

    private fun ping(owner: UdpOwner) {
        CuLog.info(CuTag.GroupChat, "Ping")
    }

    private fun write(owner: UdpOwner, bytes: ByteArray) {
        val byteBuf = ByteBuffer.wrap(bytes)
        conn?.send(byteBuf, owner.addr)
        byteBuf.clear()
    }

    private fun isReady() = conn?.isOpen ?: false

    private fun clear() {
        timing?.cancel()
        timing = null
        reading?.cancel()
        reading = null
        conn?.close()
        conn = null
        ownerDict.clear()
    }

    fun close() {
        clear()
    }
}

class UdpOwner(var addr: InetSocketAddress, var validate: String, var name: String) {
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
            return UdpMsgHead(bytes[0].toShort(), bytes[1].toShort())
        }
    }

    fun toBytes(): ByteArray {
        val bytes = ByteArray(SIZE)
        bytes[0] = secret.toByte()
        bytes[1] = event.toByte()
        return bytes
    }

}


