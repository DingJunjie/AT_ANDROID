package com.bitat.repository.dto.resp

import com.bitat.repository.dto.common.TokenDto
import kotlinx.serialization.Serializable

@Serializable
class KeySecretDto {
    var expire: Long = 0
    var prevKey: Short = 0
    var currKey: Short = 0
    var nextKey: Short = 0
    var prev: String = ""
    var curr: String = ""
    var next: String = ""
}

