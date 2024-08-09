package com.bitat.repository.socket

import com.bitat.MainCo
import com.bitat.repository.http.service.MsgReq
import com.bitat.utils.EmptyArray
import com.bitat.utils.TimeUtils
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.experimental.xor


object KeySecret {
    private var timing: Job? = null

    private var prevKey: Short = 0
    private var currKey: Short = 0
    private var nextKey: Short = 0

    private var prev: ByteArray = EmptyArray.byte
    private var curr: ByteArray = EmptyArray.byte
    private var next: ByteArray = EmptyArray.byte

    private var expire: Long = 0

    fun start() {
        timing = MainCo.launch(IO) {
            var sleep = 0L
            while (true) {
                MsgReq.secret().await().map {
                    sleep = it.expire - TimeUtils.getNow()
                    expire = it.expire
                    prevKey = it.prevKey
                    prev = it.prev.toByteArray()
                    currKey = it.currKey
                    curr = it.curr.toByteArray()
                    nextKey = it.nextKey
                    next = it.next.toByteArray()
                }
                delay(if (sleep > 0) sleep else 10000)
            }
        }
    }

    fun isValid(): Boolean = expire > TimeUtils.getNow()

    fun currentKey(): Short = currKey

    fun encryptByKey(key: Short, body: ByteArray, head: ByteArray?): ByteArray? {
        if (body.isNotEmpty()) {
            val secret = getSecret(key)
            if (secret.isNotEmpty()) {
                return encrypt(secret, body, head)
            }
        }
        return null
    }

    private fun encrypt(secret: ByteArray, body: ByteArray, head: ByteArray?): ByteArray? {
        val headLen = head?.size ?: 0
        val bodyLen = body.size
        val secretLen = secret.size
        if (secretLen == 0) {
            return null
        }
        val data = ByteArray(headLen + bodyLen)
        if (head != null) System.arraycopy(head, 0, data, 0, head.size)
        for (i in 0..<bodyLen) data[i + headLen] = body[i] xor secret[i % secretLen]
        return data
    }

    private fun getSecret(key: Short) = when (key) {
        currKey -> curr
        nextKey -> next
        prevKey -> prev
        else -> EmptyArray.byte
    }

}