package com.bitat.repository.consts

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bitat.R

enum class Visibility {
    All, Friend, Self;

    fun toCode(): Byte {
        return when (this) {
            All -> BLOG_VISIBLE_ALL.toByte()
            Friend -> BLOG_VISIBLE_FRIEND.toByte()
            Self -> BLOG_VISIBLE_SELF.toByte()
        }
    }

    companion object {
        fun getVisibility(visibility: Byte): Visibility {
            return when (visibility) {
                BLOG_VISIBLE_ALL.toByte() -> All
                BLOG_VISIBLE_FRIEND.toByte() -> Friend
                BLOG_VISIBLE_SELF.toByte() -> Self
                else -> All
            }
        }

        @Composable
        fun getUiVisibility(visibility: Visibility): String {
            return when (visibility) {
                All -> stringResource(id = R.string.visibility_all)
                Friend -> stringResource(id = R.string.visibility_friend)
                Self -> stringResource(id = R.string.visibility_self)
            }
        }
    }
}

enum class Commentable {
    All, Fans, Close, Self, Friend;

    fun toCode(): Byte {
        return when (this) {
            All -> BLOG_COMMENTABLE_ALL.toByte()
            Fans -> BLOG_COMMENTABLE_FENS.toByte()
            Friend -> BLOG_COMMENTABLE_FRIEND.toByte()
            Self -> BLOG_COMMENTABLE_SELF.toByte()
            Close -> BLOG_COMMENTABLE_CLOSE.toByte()
        }
    }

    companion object {
        fun getCommentable(commentable: Byte): Commentable {
            return when (commentable) {
                BLOG_COMMENTABLE_ALL.toByte() -> All
                BLOG_COMMENTABLE_FENS.toByte() -> Fans
                BLOG_COMMENTABLE_FRIEND.toByte() -> Friend
                BLOG_COMMENTABLE_SELF.toByte() -> Self
                BLOG_COMMENTABLE_CLOSE.toByte() -> Close
                else -> All
            }
        }

        @Composable
        fun getUiCommentable(commentable: Commentable): String {
            return when (commentable) {
                All -> stringResource(id = R.string.comment_all)
                Close -> stringResource(id = R.string.comment_close)
                Friend -> stringResource(id = R.string.comment_friend)
                Fans -> stringResource(id = R.string.comment_fans)
                Self -> stringResource(id = R.string.comment_self)
            }
        }
    }
}

enum class PublishSettings {
    SaveToGallery, SaveWatermark, Recommendable, CoCreateOptions, HDPublish, AllowDownload;

    companion object {

//        @Composable
        fun getUiTitle(settings: PublishSettings): String {
            return when (settings) {
                SaveToGallery -> "直接保存至手机"
                SaveWatermark -> "保存自己内容水印"
                Recommendable -> "可以推荐到动态"
                CoCreateOptions -> "谁可以合拍"
                HDPublish -> "高清发布"
                AllowDownload -> "允许下载"
            }
        }

    }
}

enum class Followable {
    All, Team, None, Friend, Fans, Self;

    fun toCode(): Long {
        return when (this) {
            All -> -5
            Team -> -2
            None -> 0
            Friend -> -3
            Fans -> -4
            Self -> -1
        }
    }

    companion object {
        fun getFollowable(followable: Long): Followable {
            return when (followable.toInt()) {
                -5 -> All
                -2 -> Team
                0 -> None
                -3 -> Friend
                -4 -> Fans
                -1 -> Self
                else -> All
            }
        }

        @Composable
        fun getUiFollowable(followable: Followable): String {
            return when (followable) {
                All -> stringResource(id = R.string.followable_all)
                Team -> stringResource(id = R.string.followable_team)
                None -> stringResource(id = R.string.followable_none)
                Friend -> stringResource(id = R.string.followable_friend)
                Fans -> stringResource(id = R.string.followable_fans)
                Self -> stringResource(id = R.string.followable_self)
            }
        }
    }
}