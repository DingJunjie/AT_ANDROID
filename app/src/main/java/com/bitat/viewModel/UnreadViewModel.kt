package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.MainCo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class UnreadViewModel : ViewModel() {


    fun checkUnreadMessage() {
        MainCo.launch(IO) {

        }
    }
}