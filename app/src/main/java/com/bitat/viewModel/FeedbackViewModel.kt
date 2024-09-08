package com.bitat.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.req.CreateFeedBackDto
import com.bitat.repository.dto.req.UploadTokenDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.repository.http.service.FeedBackReq
import com.bitat.repository.store.UserStore
import com.bitat.state.FeedbackViewState
import com.bitat.utils.ConstBean
import com.bitat.utils.FileType
import com.bitat.utils.ImageUtils
import com.bitat.utils.QiNiuUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


class FeedbackViewModel : ViewModel() {
    val _state = MutableStateFlow(FeedbackViewState())
    val state: StateFlow<FeedbackViewState> get() = _state.asStateFlow()

    fun feedback(content: String, kind: Int, onResult: (Boolean) -> Unit) {
        _state.update {
            it.copy(canClick = false)
        }
        MainCo.launch {

            uploadMedia()
            FeedBackReq.create(
                CreateFeedBackDto(
                    kind,
                    content,
                    _state.value.imgsQiniu.toTypedArray()
                )
            ).await().map {
                _state.update {
                    it.copy(canClick = true)
                }
                onResult(true)
            }.errMap {
                CuLog.error(
                    CuTag.Publish,
                    " FeedBackReq create error code:${it.code},msg:${it.msg}"

                )
                _state.update {
                    it.copy(canClick = true)
                }
                onResult(false)
            }
        }

    }

    fun initFeedBack(list: Array<ConstBean>) {
        _state.update {
            it.feedbackType.addAll(list)
            it
        }
    }

    fun selectFeedBack(select: ConstBean) {
        _state.update {
            it.copy(selectKind = select.type)
        }

        _state.value.feedbackType.forEachIndexed { index, constBean ->

            if (constBean.type == select.type) {
                constBean.isSelect = true
            } else {
                constBean.isSelect = false
            }
            _state.update {
                it.feedbackType[index] = constBean
                it
            }
        }
    }

    fun setContent(str: String) {
        _state.update {
            it.copy(feedbackContent = str, currentSize = str.length)
        }
    }

    fun selectImage(urls: List<Uri>) {
        _state.update {
            it.imgs.addAll(urls)
            it
        }
    }

    // 资源上传
    private suspend fun uploadMedia() {
        val cancelTag = AtomicBoolean()
        // 获取token
        LoginReq.uploadToken(UploadTokenDto(ops = 1)).await().errMap {
            CuLog.error(CuTag.Publish, it.msg)
        }.map { token ->
            _state.value.imgs.forEachIndexed { index, uri ->
                val imgParams = ImageUtils.getParams(uri)
                val key = QiNiuUtil.genKey(
                    FileType.Image, UserStore.userInfo.id, index, imgParams.width, imgParams.height
                )
                QiNiuUtil.uploadFile(
                    uri,
                    token,
                    FileType.Image,
                    key,
                    cancelTag,
                    progressFn = { a, b -> // key, percent
                        CuLog.info(CuTag.Publish, "the key $a current percentage is $b")
                    },
                ).await()

                // key | progress | response
                _state.update {
                    it.imgsQiniu.add(key)
                    it
                }
//                if (index == _state.value.imgs.size-1) successFn()
            }
        }
    }

}