package com.bitat.ui.publish

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.router.NavigationItem
import com.bitat.ui.component.BackButton
import com.bitat.viewModel.PublishViewModel

@Composable
fun VideoDisplay(
    navHostController: NavHostController,
    viewModelProvider: ViewModelProvider
) {
    val vm = viewModelProvider[PublishViewModel::class]
    val state by vm.mediaState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AsyncImage(
                model = state.localCover,
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            VideoDisplayTopBar(
                backFn = {},
                nextFn = { navHostController.navigate(NavigationItem.PublishDetail.route) })
        }
    }
}

@Composable
fun VideoDisplayTopBar(backFn: () -> Unit, nextFn: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BackButton {
            backFn()
        }
        TextButton(onClick = { nextFn() }) {
            Text("下一步")
        }
    }
}