package com.bitat.repository.store

import com.bitat.log.CuLog
import com.bitat.log.CuTag
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 *    author : shilu
 *    date   : 2024/9/6  13:34
 *    desc   :
 */
object RoomStore {
    var roomFlow = MutableSharedFlow<Int>(
        replay=1,
        extraBufferCapacity = 2,
        )
    private var deferred: Deferred<Unit>? = null

    private var count: Int = 0

    suspend fun add(any: Any) = coroutineScope {
        var deferred: Deferred<Unit>?
        var isCreator = false
        when (any) {
            any as Int -> {
                CuLog.info(CuTag.Base, "准备执行,$any")
                if (RoomStore.deferred == null) {
                    RoomStore.deferred = async {
                        delay(2000)
                        count += any + 10
                        roomFlow.emit(count)
                        roomFlow.emit(count)
                        roomFlow.emit(count)
                        roomFlow.emit(count)
                        CuLog.info(CuTag.Base, "任务执行完成,$count")
                    }
                    isCreator = true

                }
                deferred = RoomStore.deferred
                deferred?.await()
                if (isCreator) {
                    RoomStore.deferred = null
                }
            }
            else -> {

            }
        }
    }

}