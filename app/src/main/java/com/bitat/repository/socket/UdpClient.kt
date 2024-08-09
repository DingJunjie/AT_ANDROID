package com.bitat.repository.socket

import androidx.collection.MutableLongObjectMap
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
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

    private const val HB_INTERVAL = 15 * 1000L

    private val addrDict = MutableLongObjectMap<UdpAddrOwner>()

    private var conn: DatagramChannel? = null

    private var timing: Job? = null

    private var open = false

    fun start() {
        clear()
        open = true
        MainCo.launch(IO) {
            while (!KeySecret.isValid()) delay(1000)
            conn = DatagramChannel.open().apply {
                configureBlocking(false)
            }
        }
    }

    private suspend fun auth(owner: UdpAddrOwner): Boolean {
        val token = TokenStore.fetchToken()
        if (token != null) {
            println("ok")
            return true
        }
        return false
    }

    private fun read() {
        MainCo.launch(IO) {
            val byteBuf = ByteBuffer.allocate(1024)
            while (open) {
                val addr = conn?.receive(byteBuf)
                if (addr != null) {
                    byteBuf.flip()
                    val remaining = byteBuf.remaining()
                    val newBuf = ByteArray(remaining)
                    if (remaining > 0) byteBuf.get(newBuf, 0, remaining)
                }
                //50ms的延迟，监听线程得到短暂释放
                else delay(50)
            }
        }
    }

    private fun ping(owner: UdpAddrOwner) {
        CuLog.info(CuTag.GroupChat, "Ping")
    }

    private fun write(owner: UdpAddrOwner, bytes: ByteArray) {
        val byteBuf = ByteBuffer.wrap(bytes)
        conn?.send(byteBuf, owner.addr)
    }

    private fun isReady() = conn?.isOpen ?: false

    private fun isActive(owner: UdpAddrOwner) = TimeUtils.getNow() - owner.readTime < HB_INTERVAL

    private fun clear() {
        timing?.cancel()
        timing = null
        conn?.close()
        conn = null
        addrDict.clear()
    }

    fun close() {
        open = false
        clear()
    }
}

class UdpAddrOwner(val addr: InetSocketAddress, val validate: ByteArray) {
    var readTime: Long = 0
    var banChatTime: Long = 0
}


