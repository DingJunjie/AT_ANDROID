package  com.bitat.repository.http

import com.bitat.AppConfig.APP_CURRENT_PAGE
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.CuRes
import com.bitat.repository.common.INNER_ERROR
import com.bitat.repository.common.INNER_TIMEOUT
import com.bitat.repository.common.OK_CODE
import com.bitat.repository.dto.common.ResDto
import com.bitat.repository.store.TokenStore
import com.bitat.router.NavigationItem
import com.bitat.ui.common.DialogOps
import com.bitat.ui.common.showDialog
import com.bitat.utils.JsonUtils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

//响应通用类


// 封装http请求处理
object Http {
    const val HOST = "https://test.bitebei.com"

    val HttpClient = OkHttpClient()

    suspend fun <T, R> common(toJsonFn: (T) -> String, fromJsonFn: (String) -> ResDto<R>, //参数
        method: String, url: String, data: T?, headers: Map<String, String>, login: Boolean): Deferred<CuRes<R>> {
        val headerMap = HashMap<String, String>(2)
        headerMap["content-Type"] = "application/json"
        val cd = CompletableDeferred<CuRes<R>>()
        if (login) {
            val token = TokenStore.fetchToken()
            if (token != null) headerMap["Authorization"] = token
            else {
                cd.complete(CuRes.err(INNER_ERROR, "Not has token"))
                CuLog.error(CuTag.Login,"当前页面$APP_CURRENT_PAGE")
                if (APP_CURRENT_PAGE != NavigationItem.Login.route&& APP_CURRENT_PAGE!="") {
                    DialogOps("登录过期", "获取用户信息失败，请重新登录", true, {}, {}).showDialog()
                }
                return cd
            }
        }
        headerMap.putAll(headers)
        val reqBody = data?.let(toJsonFn) //CuLog.info(CuTag.Publish, reqBody ?: "")
        val req = Request.Builder().url(url).headers(headerMap.toHeaders()) //
            .method(method, reqBody?.toRequestBody()).build()

        HttpClient.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (e is java.net.SocketTimeoutException) {
                    cd.complete(CuRes.err(INNER_TIMEOUT, e.message ?: "Inner error"))
                } else {
                    cd.complete(CuRes.err(INNER_ERROR, e.message ?: "Inner error"))
                }

            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body
                if (response.isSuccessful && body != null) {
                    val resDto = fromJsonFn(body.string())
                    cd.complete(if (resDto.code == OK_CODE) {
                        if (resDto.data != null) CuRes.ok(resDto.data)
                        else CuRes.err(INNER_ERROR, "Data is null")
                    } else CuRes.err(resDto.code, resDto.msg))
                } else cd.complete(CuRes.err(INNER_ERROR, "Http status:${response.code}"))
            }

        })
        return cd
    }

    suspend inline fun <reified R> get(url: String, headers: Map<String, String> = emptyMap(), login: Boolean = true) =
        common<Unit, R>({ "" }, JsonUtils::fromJson, "GET", url, null, headers, login)

    suspend inline fun <reified T, reified R> post(url: String, data: T, headers: Map<String, String> = emptyMap(), login: Boolean = true) =
        common<T, R>(JsonUtils::toJson, JsonUtils::fromJson, "POST", url, data, headers, login)

}