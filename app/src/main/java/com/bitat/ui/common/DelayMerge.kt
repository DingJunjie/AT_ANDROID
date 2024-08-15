package com.bitat.ui.common

const val MERGE_INTERVAL = 300

data class Ops<T>(var time: Long, var t: T)

class DelayMerge<T>(private val merge: (T) -> Unit) {
    private val dict = mutableMapOf<Int, Ops<T>>()

    fun invoke(id: Int, t: T) {
        val now = System.currentTimeMillis()
        val v = dict[id]

        if (v == null) {
            val ops = Ops(now, t)
            dict[id] = ops

            Thread {
                while (true) {
                    val d = ops.time + MERGE_INTERVAL - System.currentTimeMillis()
                    if (d > 0) {
                        Thread.sleep(d)
                    } else {
                        merge(t)
                        dict.remove(id)
                        return@Thread
                    }
                }
            }.start()
        } else {
            v.time = now
        }
    }
}