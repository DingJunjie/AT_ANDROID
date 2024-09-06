package com.bitat

import androidx.compose.runtime.LaunchedEffect
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() { // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.bitat", appContext.packageName)
        runBlocking {
            RoomStore.add(12)
            RoomStore.add(1)
            RoomStore.roomFlow.collect { values ->
                CuLog.info(CuTag.Base, "任务执行完成,$values")
            }
        }


    }

    object RoomStore {
        var roomFlow = MutableSharedFlow<Int>()
        private var deferred: Deferred<Unit>? = null

        private var count: Int = 0

        suspend fun add(any: Any) = coroutineScope {
            var deferred: Deferred<Unit>?
            when (any) {
                any as Int -> {
                    if (RoomStore.deferred == null) {
                        RoomStore.deferred = async {
                            count = any + 10
                            delay(2000)
                            roomFlow.emit(count)
                        }
                    }
                    deferred = RoomStore.deferred
                    deferred?.await()

                }
                else -> {

                }
            }
        }

        suspend fun get(any: Any) = coroutineScope {
            var deferred: Deferred<Unit>?
            var isCreator = false
            when (any) {
                any as Int -> {
                    if (RoomStore.deferred == null) {
                        RoomStore.deferred = async {
                            count += any + 10
                            delay(2000)
                            roomFlow.emit(count)
                        }
                        isCreator = true
                        CuLog.info(CuTag.Base, "任务执行完成,$any")
                    }
                    deferred = RoomStore.deferred
                    deferred?.await()
                    CuLog.info(CuTag.Base, "等待执行,$any")
                    if (isCreator) {
                        RoomStore.deferred = null
                    }
                }
                else -> {

                }
            }
        }


    }

    fun addRoom() {

    }


}