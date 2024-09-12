package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.repository.common.CodeErr
import com.bitat.repository.consts.BLACKLIST
import com.bitat.repository.consts.DEFAULT
import com.bitat.repository.consts.FOLLOWED
import com.bitat.repository.dto.req.SocialDto
import com.bitat.repository.http.service.SocialReq
import com.bitat.repository.store.UserStore
import com.bitat.state.BlogState
import com.bitat.state.FollowBtnState
import com.bitat.state.SearchState
import com.bitat.utils.RelationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/8/16  16:35
 *    desc   :
 */
class FollowBtnViewModel : ViewModel() {
    val state = MutableStateFlow(FollowBtnState())

    fun followUser(rel: Int, revRel: Int, userId: Long, onSuccess: (Int) -> Unit, onError: (CodeErr) -> Unit) {
        var type: Int=DEFAULT
        when (rel) {
            DEFAULT -> {
                if (revRel != -1) { //未被对方拉黑
                    type = FOLLOWED
                } else { //已被对方拉黑
                    onError(CodeErr(-1, "关注失败，已被对方拉黑！"))
                    return
                }
            }
            FOLLOWED -> {
                type = DEFAULT
            }
            BLACKLIST -> {

            }
        }
        MainCo.launch {
            SocialReq.follow(SocialDto(type, userId)).await().map { result ->
                state.update {
                    it.copy(rel = result)
                }
                initType(result)
                onSuccess(result)
            }.errMap {
                onError(it)
            }
        }
    }


    fun initType(rel: Int) {
        val text = RelationUtils.toFollowContent(rel)
        state.update {
            it.copy(rel = rel)
        }
        state.update {
            it.copy(relContent = text)
        }
    }

}