package com.bitat.utils

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

object PlayerUtils {

    const val MAX_COUNT = 5

    val count = AtomicInteger(0)

    val playing = AtomicReference<PlayerController?>(null)

}

class PlayerController(var i: Int) {
    fun stop() {

    }
}