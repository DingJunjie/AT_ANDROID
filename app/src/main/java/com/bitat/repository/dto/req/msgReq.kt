package com.bitat.repository.dto.req

import kotlinx.serialization.Serializable

@Serializable
class FetchChatCommon{
    var ack: Boolean = false
    var fromId: Long = 0
    var time: Long = 0
    var limit: Long = 0
}
