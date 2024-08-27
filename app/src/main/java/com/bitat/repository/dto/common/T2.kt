package com.bitat.repository.dto.common

import kotlinx.serialization.Serializable

/**
 *    author : shilu
 *    date   : 2024/8/27  09:37
 *    desc   :
 */
@Serializable
class T2<T0,T1>(
    // 类型
    val v0:T0,

    val v1:T1
)