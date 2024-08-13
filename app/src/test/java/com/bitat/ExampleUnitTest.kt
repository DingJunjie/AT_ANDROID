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
        val res = cuRes()
        when (res) {
is Ok-> res.get()
        }
    }

    fun cuRes(): Res {
        return Ok("ok")
    }
}

interface Res {
    fun get(): String
}

class Ok(val str: String) : Res {
    override fun get() = str


}

class Err : Res {
    override fun get(): String {
        TODO("Not yet implemented")
    }

}