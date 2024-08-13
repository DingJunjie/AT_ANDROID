package  com.bitat.repository.common

class CodeErr(val code: Int, val msg: String)

//http响应处理
@JvmInline
@Suppress("UNCHECKED_CAST")
value class CuRes<T>(val v: Any) {
    fun isOk() = v !is CodeErr
    fun isErr() = v is CodeErr
    fun getErr(): CodeErr? = v as? CodeErr
    fun get(): T =
        if (v is CodeErr) throw Exception("CodeErr:${v.code}:${v.msg}") else v as T
    fun getOr(t: T): T = if (v is CodeErr) t else v as T
    inline fun getElse(fn: () -> T): T = if (v is CodeErr) fn() else v as T
    inline fun map(fn: (T) -> Unit) = this.apply {
        if (v !is CodeErr) fn(v as T)
    }

    inline fun errMap(fn: (CodeErr) -> Unit) = this.apply {
        if (v is CodeErr) fn(v)
    }

    companion object {
        fun <T> ok(value: Any): CuRes<T> = CuRes(value)
        fun <T> err(code: Int, msg: String): CuRes<T> = CuRes(CodeErr(code, msg))
    }

}


