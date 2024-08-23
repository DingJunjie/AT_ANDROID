package com.bitat.repository.http

import com.bitat.repository.common.CuRes
import com.bitat.repository.common.INNER_ERROR
import com.bitat.repository.http.Http.HttpClient
import com.bitat.repository.store.TokenStore
import com.bitat.utils.JsonUtils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

// 封装http请求处理
object HttpPb {
    const val HOST = "https://test.bitebei.com"
    const val PB_TYPE = "application/x-protobuf"

    suspend fun <T, R> common(
        toJsonFn: (T) -> String, fromPbFn: (ByteArray) -> R, //参数
        method: String, url: String, data: T?, headers: Map<String, String>, login: Boolean
    ): Deferred<CuRes<R>> {
        val headerMap = HashMap<String, String>(2)
        headerMap["content-Type"] = "application/json"
        val cd = CompletableDeferred<CuRes<R>>()
        if (login) {
            val token = TokenStore.fetchToken()
            if (token != null) headerMap["Authorization"] = token
            else {
                cd.complete(CuRes.err(INNER_ERROR, "not has token"))
                return cd
            }
        }
        headerMap.putAll(headers)
        val reqBody = data?.let(toJsonFn)
        //CuLog.info(CuTag.Publish, reqBody ?: "")
        val req = Request.Builder().url(url).headers(headerMap.toHeaders()) //
            .method(method, reqBody?.toRequestBody()).build()

        HttpClient.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cd.complete(CuRes.err(INNER_ERROR, e.message ?: "Inner error"))
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body
                if (response.isSuccessful && body != null) {
                    cd.complete(if (response.headers["content-Type"] == PB_TYPE) {
                        CuRes.ok(fromPbFn(body.bytes()))
                    } else JsonUtils.fromJson<ResDto<Unit>>(body.toString())
                        .run { CuRes.err(code, msg) })
                } else cd.complete(CuRes.err(INNER_ERROR, "Http status:${response.code}"))
            }

        })
        return cd
    }

    suspend inline fun <R> get(
        noinline fromPbFn: (ByteArray) -> R,
        url: String,
        headers: Map<String, String> = emptyMap(),
        login: Boolean = true
    ) = common<Unit, R>({ "" }, fromPbFn, "GET", url, null, headers, login)

    suspend inline fun <reified T, R> post(
        noinline fromPbFn: (ByteArray) -> R,
        url: String,
        data: T,
        headers: Map<String, String> = emptyMap(),
        login: Boolean = true
    ) = common<T, R>(JsonUtils::toJson, fromPbFn, "POST", url, data, headers, login)

}