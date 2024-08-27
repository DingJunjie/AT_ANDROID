package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.RankingAts
import com.bitat.repository.dto.common.PageDto
import com.bitat.repository.dto.req.SearchCommonDto
import com.bitat.repository.http.service.RankingReq
import com.bitat.repository.http.service.SearchReq
import com.bitat.state.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchState = MutableStateFlow((SearchState()))
    val searchState: StateFlow<SearchState> get() = _searchState.asStateFlow()

    init {
        _searchState.update {
            it.copy(keyword = "")
        }
    }

    fun updateKeyword(keyword: String) {
        _searchState.update {
            it.copy(keyword = keyword)
        }
    }

    fun searchUser(keyword: String) {
        MainCo.launch {
            SearchReq.searchUser(SearchCommonDto(keyword = keyword, pageNo = 0, pageSize = 20))
                .await().map { users ->
                    _searchState.update {
                        it.searchUserResult.clear()
                        it.searchUserResult.addAll(users.toList())
                        it
                    }
                }
        }
    }

    fun atRankingList() {
        MainCo.launch {
            RankingReq.rankingList(PageDto(pageSize = 6)).await().map { data ->
                CuLog.error(CuTag.Blog, "atRankingList success data:${data}")
                data.forEach { item ->
                    if (item.v0 == RankingAts) {
                        _searchState.update {
                            it.rankingList.clear()
                            it.rankingList.addAll(item.v1)
                            it
                        }
                    }
                }

            }.errMap {
                CuLog.error(CuTag.Blog, "atRankingList fail code:${it.code},msg:${it.msg}")
            }
        }
    }
}