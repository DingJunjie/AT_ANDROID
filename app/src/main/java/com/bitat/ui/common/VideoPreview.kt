package com.bitat.ui.common

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitat.ui.publish.VideoDisplayTopBar
import com.bitat.ui.video.VideoPlayer

@Composable
fun VideoPreview(uri: Uri, nextStep: (Uri) -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box {
            VideoPlayer(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .align(Alignment.BottomStart),
                uri = uri
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                VideoDisplayTopBar(backFn = { },
                    nextFn = {
                        nextStep(uri)
                    })
            }
        }
    }
}