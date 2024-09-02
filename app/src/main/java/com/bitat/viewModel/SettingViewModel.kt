package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import com.bitat.repository.http.auth.LoginReq
import com.bitat.state.SettingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {
    private val _state = MutableStateFlow((SettingState()))
    val state: StateFlow<SettingState> get() = _state.asStateFlow()

    fun loginOut(onSuccess: () -> Unit, onError: () -> Unit) {
        MainCo.launch {
            LoginReq.logout().await().map {
                onCleared()
                onSuccess()
            }.errMap {
                onError()
            }
        }
    }

    fun dialogShow(isShow: Boolean) {
        _state.update {
            it.copy(isDialogShow = isShow)
        }
    }

}