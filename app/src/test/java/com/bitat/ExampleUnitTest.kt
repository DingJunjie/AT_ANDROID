package com.bitat

import com.bitat.repository.common.CuRes
import kotlinx.coroutines.launch
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
println(255.toByte().toUByte().toShort())
    }

    private val tagDict = HashMap<String, Long>()

    fun call(str: String) {
        val tags = tags(str)
        val tagIds = tags.map { Pair(it, tagDict[it] ?: -1) }
    }

    fun tags(str: String): Array<String> {
        return arrayOf("tag")
    }

}
