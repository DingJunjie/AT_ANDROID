package com.bitat.repository.consts

/**
 *    author : shilu
 *    date   : 2024/8/20  11:06
 *    desc   : 网络请求类型
 */

//是否收藏
const val HTTP_DEFAULT = 0
const val HTTP_SUCCESS = 1
const val HTTP_FAIL = 2

const val HTTP_PAGESIZE = 10



enum class HttpLoadState {
    Success, Fail, NoData, TimeOut,Default
}