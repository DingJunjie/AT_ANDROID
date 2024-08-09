package com.bitat.repository.socket

import androidx.collection.MutableLongObjectMap
import com.bitat.MainCo
import kotlinx.coroutines.Job
import java.net.InetSocketAddress
import java.nio.channels.DatagramChannel


object UdpClient {

    private const val HB_INTERVAL = 15 * 1000L

    private val addrDict = MutableLongObjectMap<UdpAddrOwner>()

    private var conn: DatagramChannel? = null

    private var timing: Job? = null

    private var open = false

    fun start() {
      MainCo
    }
}

class UdpAddrOwner(val addr: InetSocketAddress) {
    var readTime: Long = 0
}


