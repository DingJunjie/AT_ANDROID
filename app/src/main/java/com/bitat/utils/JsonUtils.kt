package  com.bitat.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

//封装序列化工具
@OptIn(ExperimentalSerializationApi::class)
object JsonUtils {
    inline fun <reified R> fromJsonStr(json: String): R = Json.decodeFromString(json)
    inline fun <reified T> toJsonStr(data: T): String = Json.encodeToString(data)
    inline fun <reified R> fromJson(json: InputStream): R = Json.decodeFromStream(json)
    inline fun <reified T> toJson(data: T): ByteArray = ByteArrayOutputStream().also {
        Json.encodeToStream(data, it)
    }.toByteArray()
}

object RawJsonSerializer : KSerializer<RawJson> {
    override val descriptor = buildClassSerialDescriptor("RawJson")

    override fun serialize(encoder: Encoder, value: RawJson) = encoder.encodeString(value.json)

    override fun deserialize(decoder: Decoder) = RawJson(decoder.decodeString())
}

@Serializable
class RawJson(val json: String)