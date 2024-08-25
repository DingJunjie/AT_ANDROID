package  com.bitat.repository.common

const val INVALID_CREDENTIAL = 101
const val EXPIRED_CREDENTIAL = 102
const val PERMISSION_LACKED = 103
const val PERMISSION_DENIED = -103
const val EXCEPT_CODE = 104
const val ERROR_CODE = 105
const val INNER_ERROR = 106
const val INNER_TIMEOUT = 107
const val OK_CODE = 1

class CodeErr(val code: Int, val msg: String)

//http响应处理
@JvmInline
@Suppress("UNCHECKED_CAST")
value class CuRes<T>(val v: Any) {

    fun isOk() = v !is CodeErr
    fun isErr() = v is CodeErr
    fun getErr(): CodeErr = if (v is CodeErr) v else throwMsg("CuRes is ok")
    fun get(): T = if (v is CodeErr) throwMsg("CodeErr:${v.code} ~ ${v.msg}") else v as T
    fun getOr(t: T): T = if (v is CodeErr) t else v as T
    inline fun getElse(fn: () -> T): T = if (v is CodeErr) fn() else v as T
    inline fun map(fn: (T) -> Unit) = apply { if (v !is CodeErr) fn(v as T) }
    inline fun errMap(fn: (CodeErr) -> Unit) = apply { if (v is CodeErr) fn(v) }
    private fun throwMsg(msg: String): Nothing = throw RuntimeException(msg)

    companion object {
        fun <T> ok(value: T): CuRes<T> = CuRes(value as Any)
        fun <T> err(code: Int, msg: String): CuRes<T> = CuRes(CodeErr(code, msg))
    }

}


