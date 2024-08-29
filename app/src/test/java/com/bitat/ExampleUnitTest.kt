package com.bitat

import com.bitat.repository.po.SingleRoomPo
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        val list = ArrayList<SingleRoomPo>()
        val comparator = Comparator<SingleRoomPo> { r, l ->
            if (r.top == l.top) {
                (r.time - l.time).toInt()
            } else r.top - l.top
        }
        list.sortWith(comparator)
    }

    private val tagDict = HashMap<String, Long>()

    fun call(str: String) {
        val tags = tags(str)
        val tagIds = tags.map { Pair(it, tagDict[it] ?: -1) }
    }

    private fun tags(str: String): Array<String> {
        return arrayOf("tag")
    }


}
