package com.bitat.repository.singleChat

import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.KeySecret
import com.bitat.repository.singleChat.MsgDto.AckMsg
import com.bitat.repository.singleChat.MsgDto.ChatMsg
import com.bitat.repository.singleChat.MsgDto.ChatRecMsg
import com.bitat.repository.singleChat.MsgDto.NoticeMsg
import com.bitat.repository.store.TokenStore
import com.bitat.repository.store.UserStore
import com.bitat.utils.EmptyArray
import com.bitat.utils.TimeUtils
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.and
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.math.min

private val EmptyByteBuf = ByteBuffer.allocate(0)

object TcpClient {
    private const val HOST = "test.bitebei.com"
    private const val PORT = 9530
    private const val HB_INTERVAL = 15 * 1000L
    private const val MAX_INACTIVE_MS = 3 * 1000L

    private val pingBytes = TcpMsgHead(0, TcpMsgEvent.PING, 0).toBytes()

    private var conn: AsynchronousSocketChannel? = null

    private var timing: Job? = null

    private var readTime = 0L

    private var readBuf = TcpReadBuf()

    private val readHandler = object : CompletionHandler<Int, TcpReadBuf> {
        override fun completed(result: Int, readBuf: TcpReadBuf) {
            val byteBuf = readBuf.byteBuffer
            byteBuf.flip()
            var residue = readBuf.buffer.size - readBuf.bufOffset
            if (result > 0) { // 如果缓冲区没有读完，就合并到新缓冲区
                val newBuf = ByteArray(residue + result)
                if (residue > 0) System.arraycopy(
                    readBuf.buffer, readBuf.bufOffset, newBuf, 0, residue
                )
                byteBuf.get(newBuf, residue, result)
                readBuf.buffer = newBuf
                readBuf.bufOffset = 0
                residue = readBuf.buffer.size
            } else if (result < 0) conn?.close()

            while (residue >= TcpMsgHead.SIZE) { // head如果不存在，创建一个新的head
                if (readBuf.head == null) {
                    val head = TcpMsgHead.fromBytes(readBuf.buffer)
                    readBuf.bufOffset += TcpMsgHead.SIZE
                    val size = head.size // body的size为0，直接返回头部即可
                    if (size == 0) {
                        msgHandler(head, EmptyArray.byte)
                    } else { // body的size不为0，创建新body
                        readBuf.head = head
                        readBuf.body = ByteArray(size)
                    }
                }
                val head = readBuf.head
                val body = readBuf.body
                if (head != null && body != null) { // 判断有多少字节可读，如果缓冲区可读字节充足，就将body写满 // 如果缓冲区不充足，那就读完缓冲区，body在下次刷新缓冲区继续写
                    val readSize =
                        min(body.size - readBuf.bodyOffset, readBuf.buffer.size - readBuf.bufOffset)
                    if (readSize > 0) {
                        System.arraycopy(
                            readBuf.buffer, readBuf.bufOffset, body, readBuf.bodyOffset, readSize
                        )
                        readBuf.bufOffset += readSize
                        readBuf.bodyOffset += readSize // 判断body是否写完，写完就返回head和body
                        if (body.size == head.size) {
                            readBuf.head = null
                            readBuf.body = null
                            readBuf.bodyOffset = 0
                            msgHandler(head, body)
                        }
                    }
                }
                residue = readBuf.buffer.size - readBuf.bufOffset
            }
            byteBuf.clear()
            read()
        }

        override fun failed(exc: Throwable, readBuf: TcpReadBuf) {
            CuLog.error(CuTag.SingleChat, "Read bad", exc)
            readBuf.byteBuffer.clear()
            conn?.close()
        }
    }

    fun start() {
        close()
        MainCo.launch(IO) {
            while (!KeySecret.isValid()) delay(1000)
            conn = AsynchronousSocketChannel.open().apply {
                connect(InetSocketAddress(HOST, PORT), Unit, //
                    object : CompletionHandler<Void, Unit> {
                        override fun completed(result: Void?, attachment: Unit) {
                            CuLog.info(CuTag.SingleChat, "Tcp conn ok")
                            timing = MainCo.launch(IO) {
                                if (auth()) {
                                    read()
                                    delay(1000)
                                    while (isReady()) {
                                        val inactiveMs = inactiveMs()
                                        if (inactiveMs > 0) {
                                            if (inactiveMs < MAX_INACTIVE_MS) ping()
                                            else break
                                        }
                                        delay(1000)
                                    }
                                }
                                CuLog.info(CuTag.SingleChat, "Tcp timing end")
                                delay(1000)
                                start()
                            }
                        }

                        override fun failed(exc: Throwable, attachment: Unit) =
                            CuLog.error(CuTag.SingleChat, "Tcp conn err", exc)

                    })
            }
        }
    }

    private suspend fun auth(): Boolean {
        val token = TokenStore.fetchToken()
        if (token != null) {
            while (!KeySecret.isValid()) delay(1000)
            val body = token.toByteArray()
            val key = KeySecret.currentKey()
            val head = TcpMsgHead(key, TcpMsgEvent.AUTH, body.size).toBytes()
            val data = KeySecret.encryptByKey(key, body, head)
            if (data != null) {
                write(data)
                return true
            } else CuLog.error(CuTag.SingleChat, "Bad encrypt")
        } else CuLog.error(CuTag.SingleChat, "Bad token")
        return false
    }

    private fun read() {
        val byteBuffer = ByteBuffer.allocate(1024)
        readBuf.byteBuffer = byteBuffer
        conn?.read(byteBuffer, readBuf, readHandler)

    }

    private fun genMsg(event: Short, body: ByteArray): ByteArray? {
        if (KeySecret.isValid()) {
            val key = KeySecret.currentKey()
            val head = TcpMsgHead(key, event, body.size)
            return KeySecret.encryptByKey(key, body, head.toBytes())
        }
        return null
    }

    fun chat(toId: Long, kind: Int, data: ByteArray) {
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
            val msg = genMsg(TcpMsgEvent.CHAT, body.toByteArray())
            if (msg != null) write(msg)
            else CuLog.error(CuTag.SingleChat, "Bad gen msg")
        }
    }

    private fun ping() {
        write(pingBytes)
        CuLog.info(CuTag.SingleChat, "Ping")
    }

    private fun ack(toId: Long, fromId: Long, time: Long) {
        val body = AckMsg.newBuilder().also {
            it.toId = toId
            it.fromId = fromId
            it.time = time
        }.build()
        val msg = genMsg(TcpMsgEvent.ACK, body.toByteArray())
        if (msg != null) write(msg)
        else CuLog.error(CuTag.SingleChat, "Bad gen msg")
    }

    private fun chatRec(
        toId: Long, fromId: Long, toRouter: Int, //
        fromRouter: Int, time: Long, receive: Int
    ) {
        val body = ChatRecMsg.newBuilder().also {
            it.toId = toId
            it.fromId = fromId
            it.toRouter = toRouter
            it.fromRouter = fromRouter
            it.time = time
            it.receive = receive
        }.build()
        val msg = genMsg(TcpMsgEvent.CHAT_REC, body.toByteArray())
        if (msg != null) write(msg)
        else CuLog.error(CuTag.SingleChat, "Bad gen msg")
    }

    private fun write(bytes: ByteArray) {
        val byteBuf = ByteBuffer.wrap(bytes)
        conn?.write(byteBuf, byteBuf, object : CompletionHandler<Int, ByteBuffer> {
            override fun completed(result: Int, byteBuf: ByteBuffer) {
                byteBuf.clear()
            }

            override fun failed(exc: Throwable, byteBuf: ByteBuffer) {
                CuLog.error(CuTag.SingleChat, "Write bad", exc)
                byteBuf.clear()
                conn?.close()
            }

        })
    }

    private fun msgHandler(head: TcpMsgHead, body: ByteArray) {
        readTime = TimeUtils.getNow()
        val decryptBody = KeySecret.encryptByKey(head.secret, body, null) //        TODO()
        //        val selfId = UserStore.userInfo.id
        val selfId = 165L
        try {
            when (head.event) {
                TcpMsgEvent.AUTH_REC -> CuLog.info(CuTag.SingleChat, "Auth ok")
                TcpMsgEvent.PONG -> CuLog.info(CuTag.SingleChat, "Pong")
                TcpMsgEvent.ACK -> if (decryptBody != null) {
                    val msg = AckMsg.parseFrom(decryptBody)
                    if (msg.fromId == selfId) {
                        CuLog.info(CuTag.SingleChat, "Read Ack")
                    }
                }

                TcpMsgEvent.CHAT_REC -> if (decryptBody != null) {
                    val msg = ChatRecMsg.parseFrom(decryptBody)
                    if (msg.fromId == selfId) {
                        CuLog.info(CuTag.SingleChat, "Read chat rec")
                    }
                }

                TcpMsgEvent.CHAT -> if (decryptBody != null) {
                    val msg = ChatMsg.parseFrom(decryptBody)
                    CuLog.debug(CuTag.SingleChat, "收消息：toId=${msg.toId},selfId=${selfId}")
                    if (msg.toId == selfId) {
                        chatRec(msg.toId, msg.fromId, 0, msg.fromRouter, msg.time, 1)
                        TcpHandler.chat(msg)
                        CuLog.info(CuTag.SingleChat, "Read chat")
                    }
                }

                TcpMsgEvent.NOTICE -> if (decryptBody != null) {
                    val msg = NoticeMsg.parseFrom(decryptBody)
                    if (msg.toId == selfId) {
                        ack(msg.toId, msg.fromId, msg.time)
                        CuLog.info(CuTag.SingleChat, "Read notice")
                    }
                }

                else -> CuLog.error(CuTag.SingleChat, "Unknown msg")
            }
        } catch (err: Exception) {
            CuLog.error(CuTag.SingleChat, "Read error", err)
            conn?.close()
        }
    }

    private fun isReady() = conn?.isOpen ?: false

    private fun inactiveMs(): Long = TimeUtils.getNow() - readTime - HB_INTERVAL

    fun close() {
        timing?.cancel()
        timing = null
        conn?.close()
        conn = null
        readTime = 0
        readBuf.clear()
    }
}

class TcpMsgHead(val secret: Short, val event: Short, val size: Int) {

    companion object {
        const val SIZE = 4
        fun fromBytes(bytes: ByteArray): TcpMsgHead {
            var size = bytes[2] and 0xFF
            size = size shl 8 or (bytes[3] and 0xFF)
            return TcpMsgHead(bytes[0].toUByte().toShort(), bytes[1].toShort(), size)
        }
    }

    fun toBytes(): ByteArray {
        val bytes = ByteArray(SIZE)
        bytes[0] = secret.toByte()
        bytes[1] = event.toByte()
        bytes[2] = (size shr 8 and 0xFF).toByte()
        bytes[3] = (size and 0xFF).toByte()
        return bytes
    }

}

class TcpReadBuf {
    var byteBuffer: ByteBuffer = EmptyByteBuf
    var buffer = EmptyArray.byte
    var head: TcpMsgHead? = null
    var body: ByteArray? = null
    var bufOffset: Int = 0
    var bodyOffset: Int = 0

    fun clear() {
        buffer = EmptyArray.byte
        bufOffset = 0
        head = null
        body = null
        bodyOffset = 0
    }
}


object TcpMsgEvent {
    const val ACK = 0.toShort()
    const val AUTH = 1.toShort()
    const val AUTH_REC = (-AUTH).toShort()
    const val PING = 2.toShort()
    const val PONG = (-PING).toShort()
    const val CHAT = 3.toShort()
    const val CHAT_REC = (-CHAT).toShort()
    const val NOTICE = 4.toShort()
}