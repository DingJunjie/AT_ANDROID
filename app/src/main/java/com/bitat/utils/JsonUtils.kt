package  com.bitat.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//封装序列化工具
object JsonUtils {
    val instance = Json { encodeDefaults = true }
    inline fun <reified R> fromJson(json: String): R = instance.decodeFromString(json)
    inline fun <reified T> toJson(data: T): String = instance.encodeToString(data)
//    inline fun <reified R> fromJson(json: InputStream): R = instance.decodeFromStream(json)
//    inline fun <reified T> toJson(data: T): ByteArray = ByteArrayOutputStream().also {
//        instance.encodeToStream(data, it)
//    }.toByteArray()
}