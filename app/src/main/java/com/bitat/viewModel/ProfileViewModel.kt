package com.bitat.viewModel

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bitat.state.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import com.bitat.repository.http.service.SearchReq


class ProfileViewModel : ViewModel() {
    //    var userList: List<ProfileModel> = mutableStateListOf()

    val uiState = MutableStateFlow(ProfileUiState())

    val list = listOf("asdf", "steadfast", "steadfastness")

    fun closeDrawer() {
        uiState.update {
            it.copy(drawerStateValue = DrawerValue.Closed)
        }
    }

    fun switchTabbar(isTop: Boolean) {
        uiState.update {
            it.copy(isTabbarTop = isTop)
        }
    }

    fun openDrawer() {
        uiState.update {
            it.copy(drawerStateValue = DrawerValue.Open)
        }
    }

    private val someData by mutableStateOf(
        listOf(
            "water", "iris", "luv"
        )
    )

    fun addUserToList() {

    }

    fun getRecommend() = runBlocking {
        val result = SearchReq.recommendSearch(
            dto = com.bitat.repository.dto.req.RecommendSearchDto(
                pageSize = 10,
                labels = IntArray(1)
            )
        ).await()
        println(result)
    }

    init {

    }
}