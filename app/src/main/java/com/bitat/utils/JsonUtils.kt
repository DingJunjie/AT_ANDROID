package  com.bitat.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

//封装序列化工具
@OptIn(ExperimentalSerializationApi::class)
object JsonUtils {
    val instance = Json { encodeDefaults = true }
    inline fun <reified R> fromJsonStr(json: String): R = instance.decodeFromString(json)
    inline fun <reified T> toJsonStr(data: T): String = instance.encodeToString(data)
    inline fun <reified R> fromJson(json: InputStream): R = instance.decodeFromStream(json)
    inline fun <reified T> toJson(data: T): ByteArray = ByteArrayOutputStream().also {
        instance.encodeToStream(data, it)
    }.toByteArray()
}