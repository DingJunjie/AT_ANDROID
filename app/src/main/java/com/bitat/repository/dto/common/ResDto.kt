package com.bitat.repository.dto.common

import kotlinx.serialization.Serializable

@Serializable
class ResDto<T>(val msg: String, val code: Int, val data: T?)