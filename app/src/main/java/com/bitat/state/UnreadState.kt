package com.bitat.state

data class UnreadState(
    val unreadMsgCount: Int = 0,
    val unreadNoticeCount: Int = 0,

    val lastMsgId: Long = 0,
    val lastTime: Long = 0,
    val lastNoticeId: Long = 0,
    val lastNoticeTime: Long = 0
)