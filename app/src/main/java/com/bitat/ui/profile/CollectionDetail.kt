package com.bitat.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.repository.dto.resp.CollectPartDto
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.component.BackButton
import com.bitat.ui.component.MediaGrid
import com.bitat.viewModel.CollectViewModel

@Composable
fun CollectionDetail(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: CollectViewModel = viewModelProvider[CollectViewModel::class]
    val state by vm.collectState.collectAsState()

    Scaffold(topBar = {
        CollectionTopBar(state.currentCollection!!) {
            navHostController.popBackStack()
        }
    }, modifier = Modifier.padding(top = statusBarHeight)) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            MediaGrid(mediaList = state.currentCollectionItems)
        }
    }


}

@Composable
fun CollectionTopBar(collection: CollectPartDto, backFn: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        BackButton {
            backFn()
        }
        Text(collection.name)
        Text("编辑")
    }
}