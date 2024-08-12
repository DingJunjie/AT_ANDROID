package com.bitat.viewModel

import android.net.Uri
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
import com.bitat.repository.dto.req.FindFriendListDto
import com.bitat.repository.dto.req.PublishBlogDto
import com.bitat.repository.dto.req.SearchCommonDto
import com.bitat.repository.dto.req.UploadTokenDto
import com.bitat.repository.dto.resp.BlogTagDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.repository.http.service.BlogReq
import com.bitat.repository.http.service.BlogTagReq
import com.bitat.repository.http.service.SearchReq
import com.bitat.repository.http.service.UserReq
import com.bitat.repository.store.UserStore
import com.bitat.state.PublishCommonState
import com.bitat.state.PublishMediaState
import com.bitat.utils.FileType
import com.bitat.utils.ImageUtils
import com.bitat.utils.PublishUtils
import com.bitat.utils.QiNiuUtil
import com.bitat.utils.VideoUtils
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class PublishViewModel : ViewModel() {
    private val _commonState = MutableStateFlow(PublishCommonState())

    //    val imageSelectState: StateFlow<ImageSelectState> get() = _imageSelectState.asStateFlow()
    val mediaState = MutableStateFlow(PublishMediaState())
    val commonState: StateFlow<PublishCommonState> get() = _commonState.asStateFlow()

    private fun updateKind(): Unit {
        var kind = 0
        val hasPicture = mediaState.value.localImages.isNotEmpty()
        val hasVideo = mediaState.value.localVideo != Uri.EMPTY
        val text = _commonState.value.content
        if (hasPicture && !hasVideo) {
            kind = if (text === "") 2 else 3
        } else if (!hasPicture && hasVideo) {
            kind = if (text === "") 4 else 5
        } else if (hasPicture) {
            kind = if (text === "") 6 else 7
        } else {
            kind = 1
        }
        _commonState.update {
            it.copy(kind = kind.toByte())
        }
    }


    fun onContentChange(content: String) {
        _commonState.update {
            it.copy(content = content)
        }
    }

    fun searchTag(topicKeyWord: String) {
        MainCo.launch {
            BlogTagReq.find(BlogTagFindDto(searchWord = topicKeyWord, pageSize = 20, pageNo = 0))
                .await().map { res ->
                    _commonState.update {
                        it.tagSearchResult.clear()
                        it.tagSearchResult.addAll(res)
                        it
                    }
                    if (_commonState.value.tagSearchResult.isEmpty()) {
                        val defaultTag = BlogTagDto().apply {
                            id = -1
                            name = ""
                            useNum = 0
                            createTime = 0
                        }

                        _commonState.update {
                            it.tagSearchResult.add(defaultTag)
                            it
                        }
                    }
                }.errMap {
                    CuLog.error(
                        CuTag.Publish,
                        "获取#tag列表失败，接口返回：code(${it.code}),msg:${it.msg}"
                    )
                }
        }
    }

    fun searchAt(userKeyword: String = "") {
        MainCo.launch {

//            val atUser = arrayOf(
//                UserBase1Dto(
//                    id = 2435L,
//                    profile = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201809%2F03%2F20180903231510_FusSU.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1725883211&t=648f137e53de99ca6729e1fe7f560e9a",
//                    nickname = "汤姆",
//                    ats = 5
//                ), UserBase1Dto(
//                    id = 228725L,
//                    profile = "https://img1.baidu.com/it/u=1400805158,551781376&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=664",
//                    nickname = "不知名网友不知名网友不知名网友不知名网友不知名网友不知名网友不知名网友不知名网友不知名网友不知名网友",
//                    ats = 137
//                ), UserBase1Dto(
//                    id = 15649L,
//                    profile = "https://img0.baidu.com/it/u=444590157,2329884399&fm=253&fmt=auto&app=138&f=JPEG?w=570&h=570",
//                    nickname = "嬉皮猴",
//                    ats = 587
//                )
//            )

            SearchReq.searchUser(SearchCommonDto(keyword = userKeyword, pageNo = 0, pageSize = 20))
                .await().map { res ->
                    _commonState.update {
                        it.atUserSearchResult.clear()
                        it.atUserSearchResult.addAll(res)
                        it
                    }
                }.errMap {
                    CuLog.error(
                        CuTag.Publish,
                        "获取@好友列表失败，接口返回：code(${it.code}),msg:${it.msg}"
                    )
                }

//            UserReq.findFriendList(FindFriendListDto(pageSize = 20, pageNo = 0)).await()
//                .map { res ->
//                    _commonState.update {
//                        it.atUserSearchResult.clear()
//                        it.atUserSearchResult.addAll(atUser)
//                        it
//                    }
//                }.errMap {
//                    CuLog.error(
//                        CuTag.Publish, "获取@好友列表失败，接口返回：code(${it.code}),msg:${it.msg}"
//                    )
//                }
        }
    }

    fun initTags() {
        searchTag("")
    }

    fun initAt() {
        searchAt()
    }

    //话题
    fun onTopicClick(
        tag: BlogTagDto, cursorPos: Int = 0
    ): Int { //        val newContent = _commonState.value.content + "#${tag.name} ";
        val content = _commonState.value.content
        val startPos = if (cursorPos == 0) content.length else cursorPos

        _commonState.update {
            it.apply {
                tags.add(tag)
            }
        }

        if (content.isEmpty()) {
            _commonState.update {
                it.copy(content = it.content + "#" + tag.name + " ")
            }
            return content.length + tag.name.length + 2
        }

        if (content.last().toString() == "#" && startPos == content.length) {
            _commonState.update {
                it.copy(content = it.content + tag.name + " ")
            }
            return startPos + tag.name.length + 1;
        } else {
            // 中间的情况
            val contentSplitBefore = content.split("").subList(0, cursorPos + 1)
            val contentSplitAfter = content.split("").subList(cursorPos + 1, content.length + 1)
            if (contentSplitBefore.last() == "#") {
                val newContent =
                    contentSplitBefore.joinToString("") + tag.name + contentSplitAfter.joinToString(
                        ""
                    ) + " "
                _commonState.update {
                    it.copy(content = newContent)
                }
                return contentSplitBefore.size + tag.name.length;
            } else {
                val newContent =
                    contentSplitBefore.joinToString("") + "#" + tag.name + contentSplitAfter.joinToString(
                        ""
                    ) + " "
                _commonState.update {
                    it.copy(content = newContent)
                }
                return contentSplitBefore.size + tag.name.length + 1;
            }

        }

    }

    fun onAtClick(user: UserBase1Dto, cursorPos: Int): Int {
        val content = _commonState.value.content
        val startPos = if (cursorPos == 0) content.length else cursorPos

        _commonState.update {
            it.apply {
                atUsers.add(user)
            }
        }


        if (content.isEmpty()) {
            _commonState.update {
                it.copy(content = it.content + "@" + user.nickname + " ")
            }
            return content.length + user.nickname.length + 2
        }

        if (content.last().toString() == "@" && startPos == content.length) {
            _commonState.update {
                it.copy(content = it.content + user.nickname + " ")
            }
            return startPos + user.nickname.length + 1;
        } else {
            // 中间的情况
            val contentSplitBefore = content.split("").subList(0, cursorPos + 1)
            val contentSplitAfter = content.split("").subList(cursorPos + 1, content.length + 1)
            if (contentSplitBefore.last() == "@") {
                val newContent =
                    contentSplitBefore.joinToString("") + user.nickname + contentSplitAfter.joinToString(
                        ""
                    ) + " "
                _commonState.update {
                    it.copy(content = newContent)
                }
                return contentSplitBefore.size + user.nickname.length;
            } else {
                val newContent =
                    contentSplitBefore.joinToString("") + "@" + user.nickname + contentSplitAfter.joinToString(
                        ""
                    ) + " "
                _commonState.update {
                    it.copy(content = newContent)
                }
                return contentSplitBefore.size + user.nickname.length + 1;
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
        _commonState.update {
            it.copy(followId = followable.toCode())
        }
    }

    fun onVisibilityClick(visibility: Visibility) {
        _commonState.update {
            it.copy(visibility = visibility)
        }
    }

    fun onCommentableClick(commentable: Commentable) {
        _commonState.update {
            it.copy(commentable = commentable)
        }
    }

    fun updateVisibility(v: Visibility): Unit {
        _commonState.update {
            it.copy(visibility = v)
        }
    }

    fun updateCommentable(c: Commentable): Unit {
        _commonState.update {
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
            it.copy(
                currentImage = path.last()
            )
        }
    }

    fun setCurrentImage(uri: Uri) {
        mediaState.update {
            it.copy(currentImage = uri)
        }
    }

    fun addVideo(path: Uri) {
        MainCo.launch { //            val cover = VideoUtils.getCover(path.toString())
            mediaState.update {
                it.copy(localVideo = path)
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
                    FileType.Image, UserStore.userInfo.id, index, imgParams.width, imgParams.height
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

                //                val coverParams = ImageUtils.getParams(mediaState.value.localCover)
                //                val coverKey = QiNiuUtil.genKey(
                //                    FileType.Image,
                //                    UserStore.userInfo.id,
                //                    0,
                //                    coverParams.width,
                //                    coverParams.height
                //                )
                //
                //                QiNiuUtil.uploadFile(
                //                    mediaState.value.localCover,
                //                    token,
                //                    FileType.Image,
                //                    coverKey,
                //                    cancelTag,
                //                    progressFn = { a, b ->
                //                    }
                //                ).await()

                // key | progress | response
                mediaState.update { state ->
                    state.copy(video = key)
                }
            }
        }
    }

    private fun publishMedia(completeFn: () -> Unit) {

        var newContent =
            PublishUtils.convertContentWithTag(_commonState.value.content, _commonState.value.tags)
        newContent =
            PublishUtils.covertContentWithAt(newContent, _commonState.value.atUserSearchResult)


        val dto = PublishBlogDto().apply {
            adCode = _commonState.value.adCode
            longitude = _commonState.value.longitude
            latitude = _commonState.value.latitude
            location = _commonState.value.location

//            content = _commonState.value.content
            content = newContent
            vote = mediaState.value.vote
            musicId = _commonState.value.musicId
            atUsers =
                _commonState.value.atUsers.map {
                    it.id
                }.toLongArray() //            tags=_commonState.value.tags.
            openComment = _commonState.value.commentable.toCode()
            visible = _commonState.value.visibility.toCode()
            albumOps =
                if (mediaState.value.followId > 0) mediaState.value.followId else mediaState.value.albumOps.toCode()
        }

        MainCo.launch {
            uploadMedia()

            dto.kind = _commonState.value.kind
            dto.resource = ResourceDto()
            dto.resource.images = mediaState.value.images.toTypedArray()
            dto.resource.video = mediaState.value.video
            dto.cover = mediaState.value.cover

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
                adCode = _commonState.value.adCode
                longitude = _commonState.value.longitude
                latitude = _commonState.value.latitude
                location = _commonState.value.location
                content = _commonState.value.content
                visible = _commonState.value.visibility.toCode()
                atUsers = _commonState.value.atUsers.map {
                    it.id
                }.toLongArray()
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
                    completeFn()
                }
            }
        }
    }

    fun publish(completeFn: () -> Unit) {
        _commonState.update {
            it.copy(isPublishClick = false)
        }
        updateKind()


        val kind = _commonState.value.kind
        if (kind.toInt() == 1) {
            publishText {
                completeFn()
                _commonState.update {
                    it.copy(isPublishClick = true)
                }
            }
        } else {
            publishMedia {
                completeFn()
                _commonState.update {
                    it.copy(isPublishClick = true)
                }
            }
        }
    }

    fun locationUpdate(point: LatLonPoint, addName: String) {
        CuLog.debug(CuTag.Publish, "获取到定位$addName,${point.latitude}")
        _commonState.update {
            it.copy(longitude = point.longitude, latitude = point.latitude, location = addName)
        }
    }

    fun saveDraft() {
        ToastModel("存草稿", ToastModel.Type.Normal).showToast()
    }

    fun clearData() {
        _commonState.apply {
            PublishCommonState()
        }

        mediaState.apply {
            PublishMediaState()
        }
    }

}
























