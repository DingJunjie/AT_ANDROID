package com.bitat.viewModel

import android.content.Context
import android.net.Uri
import android.os.FileUtils
import androidx.camera.core.internal.utils.VideoUtil
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.amap.api.services.core.LatLonPoint
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.Commentable
import com.bitat.repository.consts.Followable
import com.bitat.repository.consts.Visibility
import com.bitat.repository.dto.common.ResourceDto
import com.bitat.repository.dto.req.BlogTagFindDto
import com.bitat.repository.dto.req.PublishBlogDto
import com.bitat.repository.dto.req.UploadTokenDto
import com.bitat.repository.dto.resp.BlogTagDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.repository.http.service.BlogReq
import com.bitat.repository.http.service.BlogTagReq
import com.bitat.repository.store.UserStore
import com.bitat.state.PublishCommonState
import com.bitat.state.PublishMediaState
import com.bitat.utils.FileType
import com.bitat.utils.ImageUtils
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.VideoUtils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class PublishViewModel : ViewModel() {
    val mediaState = MutableStateFlow(PublishMediaState())
    val commonState = MutableStateFlow(PublishCommonState())


    private fun updateKind(): Unit {
        var kind = 0
        val hasPicture = mediaState.value.localImages.isNotEmpty()
        val hasVideo = mediaState.value.localVideo != Uri.EMPTY
        val text = commonState.value.content
        if (hasPicture && !hasVideo) {
            kind = if (text === "") 2 else 3
        } else if (!hasPicture && hasVideo) {
            kind = if (text === "") 4 else 5
        } else if (hasPicture) {
            kind = if (text === "") 6 else 7
        } else {
            kind = 1
        }
        commonState.update {
            it.copy(kind = kind.toByte())
        }
    }


    fun onContentChange(content: String) {
        commonState.update {
            it.copy(content = content)
        }
    }

    fun searchTopic(topicKeyWord: String) {
        MainCo.launch {
            BlogTagReq.find(BlogTagFindDto(searchWord = topicKeyWord, pageSize = 20, pageNo = 0))
                .await().map { res ->
                    commonState.update {
                        it.tagSearchResult.clear()
                        it.tagSearchResult.addAll(res)
                        it
                    }
                    if (commonState.value.tagSearchResult.isEmpty()) {
                        val defaultTag = BlogTagDto().apply {
                            id = -1
                            name = ""
                            useNum = 0
                            createTime = 0
                        }

                        commonState.update {
                            it.tagSearchResult.add(defaultTag)
                            it
                        }
                    }
                }.errMap {
                    CuLog.error(CuTag.Publish, "get tags error $it")
                }
        }
    }

    fun initTags() {
        searchTopic("")
    }

    //话题
    fun onTopicClick(tag: BlogTagDto) { //        val newContent = commonState.value.content + "#${tag.name} ";
        val content = commonState.value.content
        if (content.last().toString() == "#") {
            commonState.update {
                it.copy(content = it.content + tag.name + " ")
            }
        } else {
            val contentSplit = content.split("#")
            val newContent =
                contentSplit.subList(0, contentSplit.size - 1)
                    .joinToString { "#" } + "#" + tag.name + " "
            commonState.update {
                it.copy(content = newContent)
            }
        }

        commonState.update {
            it.apply {
                tags.add(tag)
            }
        }

    }

    fun removeMedia(uri: Uri) {
        mediaState.update {
            it.localImages.remove(uri)
            it
        }
    }

    fun onAtClick() {

    }

    fun onFollowClick(followable: Followable) {
        commonState.update {
            it.copy(followId = followable.toCode())
        }
    }

    fun onVisibilityClick(visibility: Visibility) {
        commonState.update {
            it.copy(visibility = visibility)
        }
    }

    fun updateVisibility(v: Visibility): Unit {
        commonState.update {
            it.copy(visibility = v)
        }
    }

    fun updateCommentable(c: Commentable): Unit {
        commonState.update {
            it.copy(commentable = c)
        }
    }

    fun updateFollowable(f: Followable): Unit {
        mediaState.update {
            it.copy(albumOps = f)
        }
    }

    fun addPicture(path: List<Uri>) {
        mediaState.update {
            it.localImages.addAll(path)
            it.currentImage.apply {
                path.last()
            }
            it
        }
    }

    fun setCurrentImage(uri: Uri) {
        mediaState.update {
            it.copy(currentImage = uri)
        }
    }

    fun addVideo(path: Uri) {
        MainCo.launch {
            val cover = VideoUtils.getCover(path.toString())
            mediaState.update {
                it.copy(localVideo = path, localCover = cover)
            }
        }

    }

    fun addAudio(path: Uri) {
        mediaState.update {
            it.copy(localAudio = path)
        }
    }

    fun selectImage(uri: Uri) {
        mediaState.update {
            it.copy(currentImage = uri)
        }
    }

    private suspend fun uploadMedia() {
        val cancelTag = AtomicBoolean()
        LoginReq.uploadToken(UploadTokenDto(ops = 1)).await().errMap {
            CuLog.error(CuTag.Publish, it.msg)
        }.map { token ->
            mediaState.value.localImages.forEachIndexed { index, it ->
                val cd = CompletableDeferred<Boolean>()
                val imgParams = ImageUtils.getParams(it)
                val key = QiNiuUtil.genKey(
                    FileType.Image,
                    UserStore.userInfo.id,
                    index,
                    imgParams.width,
                    imgParams.height
                )
                QiNiuUtil.uploadFile(
                    it,
                    token,
                    FileType.Image,
                    key,
                    cancelTag,
                    progressFn = { a, b -> // key, percent
                        CuLog.info(CuTag.Publish, "the key $a current percentage is $b")
                    },
                ).await()

                // key | progress | response
                mediaState.update { img ->
                    img.images.add(key)
                    img.copy(cover = img.images.first())
                }
            }

            val video = mediaState.value.localVideo.path;
            if (!video.isNullOrBlank()) {
                val videoParams = VideoUtils.getParams(mediaState.value.localVideo)
                val key = QiNiuUtil.genKey(
                    FileType.Video,
                    UserStore.userInfo.id,
                    0,
                    videoParams.width,
                    videoParams.height,
                    videoParams.duration
                )
                QiNiuUtil.uploadFile(
                    mediaState.value.localVideo,
                    token,
                    FileType.Video,
                    key,
                    cancelTag,
                    progressFn = { a, b ->
                    },
                ).await()

                val coverParams = ImageUtils.getParams(mediaState.value.localCover)
                val coverKey = QiNiuUtil.genKey(
                    FileType.Image,
                    UserStore.userInfo.id,
                    0,
                    coverParams.width,
                    coverParams.height
                )

                QiNiuUtil.uploadFile(
                    mediaState.value.localCover,
                    token,
                    FileType.Image,
                    coverKey,
                    cancelTag,
                    progressFn = { a, b ->
                    }
                ).await()

                // key | progress | response
                mediaState.update { state ->
                    state.copy(video = key, cover = coverKey)
                }
            }
        }
    }

    fun publishMedia(completeFn: () -> Unit) {

        val dto = PublishBlogDto().apply {
            adCode = commonState.value.adCode
            longitude = commonState.value.longitude
            latitude = commonState.value.latitude
            location = commonState.value.location
            cover = mediaState.value.cover
            content = commonState.value.content
            vote = mediaState.value.vote
            musicId = commonState.value.musicId


            openComment = commonState.value.commentable.toCode()
            visible = commonState.value.visibility.toCode()
            albumOps =
                if (mediaState.value.followId > 0) mediaState.value.followId else mediaState.value.albumOps.toCode()
        }

        MainCo.launch {
            uploadMedia()

            dto.kind = commonState.value.kind
            dto.resource = ResourceDto()
            dto.resource.images = mediaState.value.images.toTypedArray()
            dto.resource.video = mediaState.value.video

            if (dto.resource.video != "") { // 设置视频封面
            } else if (dto.resource.images.isNotEmpty()) {
                dto.cover = dto.resource.images[0]
            }

            CuLog.info(CuTag.Publish, dto.toString())

            val result = BlogReq.publish(dto).await()
            result.map {
                completeFn()
                CuLog.info(CuTag.Publish, "publish success $it")
            }.errMap {
                CuLog.info(CuTag.Publish, "publish failed, message is ${it.msg}")

            }
        }
    }

    private fun publishText(completeFn: () -> Unit) { //发布逻辑
        MainCo.launch {
            val dto = PublishBlogDto().apply {
                adCode = commonState.value.adCode
                longitude = commonState.value.longitude
                latitude = commonState.value.latitude
                location = commonState.value.location
                content = commonState.value.content

                visible = commonState.value.visibility.toCode()
            }

            val cancelTag = AtomicBoolean()
            MainCo.launch {
                dto.kind = 1

                val result = BlogReq.publish(dto).await()
                result.map {
                    completeFn()
                    println("update success $it")
                }.errMap {
                    println("update failed, message is ${it.msg}")
                }
            }
        }
    }

    fun publish(completeFn: () -> Unit) {
        updateKind()

        val kind = commonState.value.kind
        if (kind.toInt() == 1) {
            publishText { completeFn() }
        } else {
            publishMedia { completeFn() }
        }
    }

    fun locationUpdate(point: LatLonPoint, addName: String) {
        CuLog.debug(CuTag.Publish, "获取到定位$addName,${point.latitude}")
        commonState.update {
            it.copy(longitude = point.longitude, latitude = point.latitude, location = addName)
        }
    }
}
























