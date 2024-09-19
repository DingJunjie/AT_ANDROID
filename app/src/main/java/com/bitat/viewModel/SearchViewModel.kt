package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.tokenErrorOpt
import com.bitat.repository.consts.RankingAts
import com.bitat.repository.dto.common.PageDto
import com.bitat.repository.dto.common.TagsDto
import com.bitat.repository.dto.req.FindSearchTagDto
import com.bitat.repository.dto.req.SearchCommonDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.http.service.RankingReq
import com.bitat.repository.http.service.SearchReq
import com.bitat.repository.po.SearchHistoryPo
import com.bitat.repository.sqlDB.SearchHistoryDB
import com.bitat.repository.store.UserStore
import com.bitat.state.SearchState
import com.bitat.utils.TimeUtils
import kotlinx.coroutines.Dispatchers.IO
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

    fun deleteKeyword(keyword: String) {
        MainCo.launch(IO) {
            SearchHistoryDB.delete(UserStore.userInfo.id, keyword)
            _searchState.update {
                it.historyList.removeIf { that ->
                    that.content == keyword
                }
                it
            }
        }
    }

    fun updateKeyword(keyword: String) {
        _searchState.update {
            it.copy(keyword = keyword)
        }
    }

    fun insertHistory() {
        SearchHistoryDB.insertOne(SearchHistoryPo().apply {
            userId = UserStore.userInfo.id
            content = _searchState.value.keyword
            time = TimeUtils.getNow()
        })
    }

    fun getHistory() {
        MainCo.launch(IO) {
            val list = SearchHistoryDB.find(UserStore.userInfo.id)
            _searchState.update {
                it.historyList.clear()
                it.historyList.addAll(list)
                it
            }
        }
    }

    fun searchVideo(keyword: String) {
        MainCo.launch {
            SearchReq.searchVideo(SearchCommonDto(keyword = keyword, pageNo = 0, pageSize = 20))
                .await().map { videos ->
                    _searchState.update {
                        it.searchVideoResult.clear()
                        it.searchVideoResult.addAll(videos)
                        it
                    }
                }
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
                if (data.isNotEmpty()) //
                    if (data.last().v0 == RankingAts) {
                        _searchState.update {
                            it.rankingList.clear()
                            it.rankingList.addAll(data.first().v1)
                            it
                        }
                    }

            }.errMap {
                CuLog.error(CuTag.Blog, "atRankingList fail code:${it.code},msg:${it.msg}")
            }
        }
    }

    fun updateUser(user: UserBase1Dto) {
        val index = _searchState.value.searchUserResult.indexOf(user)
        if (index >= 0) _searchState.update {
            it.searchUserResult[index] = user
            it
        }
        _searchState.update {
            it.copy(flag = it.flag + 1)
        }
    }

    fun search(searchStr: String) {
        viewModelScope.launch {
            SearchReq.search(SearchCommonDto(keyword = searchStr)).await().map { res ->
                _searchState.update {
                    it.searchResult.clear()
                    it.searchResult.addAll(res)
                    it
                }
            }.errMap(::tokenErrorOpt)
        }

    }

    fun searchTag(tag: TagsDto, blogId: Long) {
        viewModelScope.launch {
            SearchReq.findSearchTag(FindSearchTagDto(tag = tag, blogId = blogId)).await()
                .map { res ->
                    _searchState.update {
                        it.searchResult.addAll(res)
                        it
                    }
                }.errMap(::tokenErrorOpt)
        }
    }
}