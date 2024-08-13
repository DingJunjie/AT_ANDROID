package  com.bitat.repository.common

class CodeErr(val code: Int, val msg: String)

//http响应处理
@JvmInline
@Suppress("UNCHECKED_CAST")
value class CuRes<T>(val v: Any) {
    fun isOk() = v !is CodeErr
    fun isErr() = v is CodeErr
    fun getErr(): CodeErr = if (v is CodeErr) v else throwMsg("CodeErr is ok")
    fun get(): T = if (v is CodeErr) throwMsg("CodeErr:${v.code} ~ ${v.msg}") else v as T
    fun getOr(t: T): T = if (v is CodeErr) t else v as T
    inline fun getElse(fn: () -> T): T = if (v is CodeErr) fn() else v as T
    inline fun map(fn: (T) -> Unit) = this.apply {
        if (v !is CodeErr) fn(v as T)
    }

    inline fun errMap(fn: (CodeErr) -> Unit) = this.apply {
        if (v is CodeErr) fn(v)
    }

    private fun throwMsg(msg: String): Nothing = throw RuntimeException(msg)

    companion object {
        fun <T> ok(value: Any): CuRes<T> = CuRes(value)
        fun <T> err(code: Int, msg: String): CuRes<T> = CuRes(CodeErr(code, msg))
    }

}


