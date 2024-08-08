package  com.bitat.repository.common

class CodeErr(val code: Int, val msg: String)

//http响应处理
@JvmInline
@Suppress("UNCHECKED_CAST")
value class CuRes<T>(val value: Any) {
    fun isOk() = value !is CodeErr
    fun isErr() = value is CodeErr
    fun getErr(): CodeErr? = value as? CodeErr
    fun get(): T? = if (value is CodeErr) null else value as T
    fun getOr(t: T): T = if (value is CodeErr) t else value as T
    inline fun getElse(fn: () -> T): T = if (value is CodeErr) fn() else value as T
    inline fun map(fn: (T) -> Unit) = this.apply {
        if (value !is CodeErr) fn(value as T)
    }

    inline fun errMap(fn: (CodeErr) -> Unit) = this.apply {
        if (value is CodeErr) fn(value)
    }

    companion object {
        fun <T> ok(value: Any): CuRes<T> = CuRes(value)
        fun <T> err(code: Int, msg: String): CuRes<T> = CuRes(CodeErr(code, msg))
    }

}


