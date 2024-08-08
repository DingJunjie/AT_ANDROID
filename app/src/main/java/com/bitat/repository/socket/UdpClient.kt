package com.bitat.repository.socket

import kotlinx.coroutines.Job
import java.net.InetSocketAddress
import java.nio.channels.DatagramChannel


object UdpClient {

    private const val HB_INTERVAL = 15 * 1000L

    private var connArr = ArrayList<UdpAddrOwner>()

    private var conn: DatagramChannel? = null

    private var timing: Job? = null

    private var open = false

    fun test() {

    }
}

class UdpAddrOwner(val id: Long, val addr: InetSocketAddress) {
    var readTime: Long = 0
}


