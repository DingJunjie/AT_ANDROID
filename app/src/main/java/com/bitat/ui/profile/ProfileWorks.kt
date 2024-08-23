package com.bitat.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.ui.component.MediaGrid
import com.bitat.viewModel.ProfileViewModel

@Composable
fun ProfileWorks(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: ProfileViewModel = viewModelProvider[ProfileViewModel::class]
    val state by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.getMyWorks()
    }

    MediaGrid(mediaList = state.myWorks)
}