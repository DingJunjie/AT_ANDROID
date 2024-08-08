package com.bitat

import com.bitat.repository.http.ResDto
import com.bitat.utils.JsonUtils
import com.bitat.utils.RawJson
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val str = JsonUtils.toJsonStr(
            ResDto("ok", 1, RawJson("""{"name":"hehe"}""")))
        println(str)
        JsonUtils.fromJsonStr<ResDto<RawJson>>(str)
    }
}