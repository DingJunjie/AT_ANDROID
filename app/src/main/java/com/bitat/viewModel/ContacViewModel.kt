package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitat.repository.common.tokenErrorOpt
import com.bitat.repository.dto.req.FindFriendListDto
import com.bitat.repository.http.service.UserReq
import com.bitat.state.CollectState
import com.bitat.state.ContactState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *    author : shilu
 *    date   : 2024/9/19  17:19
 *    desc   :
 */
class ContactViewModel : ViewModel() {

    private val _state = MutableStateFlow(ContactState())
    val state: StateFlow<ContactState> get() = _state.asStateFlow()

    fun getContact(isInit: Boolean) {
        viewModelScope.launch {
            UserReq.findFriendList(FindFriendListDto()).await().map { list ->
                _state.update {
                    if (isInit)
                        it.contactList.clear()
                    it.contactList.addAll(list)
                    it
                }
                _state.update {
                    it.copy(flag = it.flag + 1)
                }
            }.errMap(::tokenErrorOpt)


        }
    }

}