package com.bitat.ui.publish

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun PictureDisplay(
    navHostController: NavHostController,
    viewModelProvider: ViewModelProvider
) {
    val vm = viewModelProvider[PublishViewModel::class]
    val state by vm.mediaState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = state.currentImage,
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
            PictureDisplayTopBar(backFn = {
                navHostController.popBackStack()
            }, nextFn = {
                navHostController.navigate(NavigationItem.PublishDetail.route)
            })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)
            ) {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(state.localImages.size) { index ->
                        ImageBox(state.localImages[index], setCurrentFn = {
                            vm.setCurrentImage(it)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ImageBox(uri: Uri, setCurrentFn: (Uri) -> Unit) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(100.dp)
            .padding(10.dp)
            .clickable { setCurrentFn(uri) }
    ) {
        AsyncImage(model = uri, contentDescription = "")
    }
}

@Composable
fun PictureDisplayTopBar(backFn: () -> Unit, nextFn: () -> Unit) {
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