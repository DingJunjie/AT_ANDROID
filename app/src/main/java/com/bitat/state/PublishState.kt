package com.bitat.state

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.repository.consts.*
import com.bitat.repository.dto.common.TagsDto
import com.bitat.repository.dto.resp.BlogTagDto
import com.bitat.repository.dto.resp.UserDto


data class PublishCommonState(
    val adCode: String = "",
    val visibility: Visibility = Visibility.All,
    val kind: Byte = 2,
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val location: String = "",
    val musicId: Long = -1,
    val musicKind: Byte = 0,
    val followId: Long = -1,
    val commentable: Commentable = Commentable.All,
    val showLocation: Boolean = true,
    val tagSearchResult: SnapshotStateList<BlogTagDto> = mutableStateListOf(),
    val tags: ArrayList<BlogTagDto> = arrayListOf(),
    val atUserSearchResult: SnapshotStateList<UserDto> = mutableStateListOf(),
    val atUsers: ArrayList<Int> = arrayListOf(),
    val content: String = "",
)

data class PublishMediaState(
    val cover: String = "",
    val localCover: Uri = Uri.EMPTY,
    val vote: Byte = 0,
    val coCreates: ArrayList<Int> = arrayListOf(),
    val albumOps: Followable = Followable.None,
    val albumMember: ArrayList<Int> = arrayListOf(),
    val followId: Long = -1,
    val images: ArrayList<String> = arrayListOf(),
    val currentImage: Uri = Uri.EMPTY,
    val localImages: SnapshotStateList<Uri> = mutableStateListOf(),
    val video: String = "",
    val localVideo: Uri = Uri.EMPTY,
    val localAudio: Uri = Uri.EMPTY,
    val audio: String = ""
)
