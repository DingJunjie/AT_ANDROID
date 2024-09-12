package com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class FetchChatCommon{
    var ack: Boolean = false
    var limit: Int = 10
    var fromId: Long = 0
    var time: Long = 0

}
