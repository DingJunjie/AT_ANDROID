package com.bitat.ui.common

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.router.NavigationItem
import com.bitat.ui.publish.createImageCaptureUseCase
import com.bitat.ui.publish.createVideoCaptureUseCase
import com.bitat.ui.publish.getCameraProvider
import com.bitat.ui.publish.startRecordingVideo
import com.bitat.ui.publish.takePhoto
import com.bitat.viewModel.PublishViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SingleFuncCamera(
    opt: ActivityResultContracts.PickVisualMedia.VisualMediaType,
    completeFn: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    var isFirstCreate by remember {
        mutableStateOf(true)
    }
    val lifecycleOwner by rememberUpdatedState(androidx.compose.ui.platform.LocalLifecycleOwner.current)
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            //            android.Manifest.permission.READ_MEDIA_AUDIO,
            //            android.Manifest.permission.READ_MEDIA_VIDEO,
            //            android.Manifest.permission.READ_MEDIA_IMAGES,
        )
    )

    val previewView: PreviewView = remember {
        PreviewView(context).apply { //            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }
    }

    var recording: Recording? = remember { null }

    val audioEnabled: MutableState<Boolean> = remember { mutableStateOf(false) }
    val videoCapture: MutableState<VideoCapture<Recorder>?> = remember { mutableStateOf(null) }
    val recordingStarted: MutableState<Boolean> = remember { mutableStateOf(false) }

    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().build()
    }

    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }

    val selector =
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

    DisposableEffect(lifecycleOwner) {
        val lifeCycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    CuLog.info(CuTag.Blog, "PublishPage------------->>>> ON_CREATE")
                }

                Lifecycle.Event.ON_START -> {
                    CuLog.info(CuTag.Blog, "PublishPage------------->>>> ON_START")
                }

                Lifecycle.Event.ON_STOP -> {
                    CuLog.info(CuTag.Publish, "PublishPage------------->>>> ON_STOP")
                }

                Lifecycle.Event.ON_RESUME -> {
                    CuLog.info(CuTag.Publish, "PublishPage------------->>>> ON_RESUME")
                }

                Lifecycle.Event.ON_PAUSE -> {
                    CuLog.info(CuTag.Publish, "PublishPage------------->>>> ON_PAUSE")
                }

                Lifecycle.Event.ON_DESTROY -> {
                    CuLog.info(CuTag.Publish, "PublishPage------------->>>> ON_DESTROY")
                }

                else -> {}
            }

        }
        lifecycleOwner.lifecycle.addObserver(lifeCycleObserver)
        onDispose {
            CuLog.info(CuTag.Publish, "PublishPage------------->>>> onDispose")
            lifecycleOwner.lifecycle.removeObserver(lifeCycleObserver)
        }
    }

    val dialogState = rememberDialogState()

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(previewView) {
        videoCapture.value = context.createVideoCaptureUseCase(
            lifecycleOwner, cameraSelector = cameraSelector.value, previewView
        )

        context.createImageCaptureUseCase(
            lifecycleOwner, cameraSelector = cameraSelector.value, imageCapture
        )
    }

    LaunchedEffect(CameraSelector.LENS_FACING_BACK) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        videoCapture.value = context.createVideoCaptureUseCase(
            lifecycleOwner, cameraSelector = cameraSelector.value, previewView
        )
        context.createImageCaptureUseCase(
            lifecycleOwner, cameraSelector = cameraSelector.value, imageCapture
        )
    }


    if (permissionState.allPermissionsGranted) {
        // 全部允许
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { previewView }, modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = {
                            if (!recordingStarted.value) {
                                videoCapture.value?.let { videoCapture ->
                                    recordingStarted.value = true
                                    val mediaDir = context.externalCacheDirs
                                        .firstOrNull()
                                        ?.let {
                                            File(it, "at").apply { mkdirs() }
                                        }

                                    recording = startRecordingVideo(
                                        context = context,
                                        filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                                        videoCapture = videoCapture,
                                        outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir,
                                        executor = context.mainExecutor,
                                        audioEnabled = audioEnabled.value
                                    ) { event ->
                                        if (event is VideoRecordEvent.Finalize) {
                                            val uri = event.outputResults.outputUri
                                            if (uri != Uri.EMPTY) {
                                                val uriEncoded = URLEncoder.encode(
                                                    uri.toString(),
                                                    StandardCharsets.UTF_8.toString()
                                                )

                                                completeFn(uri)
                                            }
                                        }
                                    }
                                }
                            }
                        }, onTap = {

                            if (opt == ImagePickerOption.VideoOnly) {
                                if (!recordingStarted.value) {
                                    videoCapture.value?.let { videoCapture ->
                                        recordingStarted.value = true
                                        val mediaDir = context.externalCacheDirs
                                            .firstOrNull()
                                            ?.let {
                                                File(it, "at").apply { mkdirs() }
                                            }

                                        recording = startRecordingVideo(
                                            context = context,
                                            filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                                            videoCapture = videoCapture,
                                            outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir,
                                            executor = context.mainExecutor,
                                            audioEnabled = audioEnabled.value
                                        ) { event ->
                                            if (event is VideoRecordEvent.Finalize) {
                                                val uri = event.outputResults.outputUri
                                                if (uri != Uri.EMPTY) {
                                                    completeFn(uri)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    recordingStarted.value = false
                                    recording?.stop()
                                }
                            } else {
                                val mediaDir = context.externalCacheDirs
                                    .firstOrNull()
                                    ?.let {
                                        File(it, "at").apply { mkdirs() }
                                    }
                                // 拍照
                                takePhoto(context,
                                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                                    imageCapture,
                                    outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir,
                                    executor = context.mainExecutor,
                                    onError = {},
                                    onImageCaptured = { uri ->
                                        completeFn(uri)
                                    })
                            }
                        })
                    },
            ) {
                Icon(
                    painter = painterResource(if (recordingStarted.value) R.drawable.logo else R.drawable.nav_add),
                    contentDescription = "",
                    modifier = Modifier.size(64.dp)
                )
            }


            if (!recordingStarted.value) {
                IconButton(
                    onClick = {
                        cameraSelector.value =
                            if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                            else CameraSelector.DEFAULT_BACK_CAMERA
                        lifecycleOwner.lifecycleScope.launch {
                            videoCapture.value = context.createVideoCaptureUseCase(
                                lifecycleOwner = lifecycleOwner,
                                cameraSelector = cameraSelector.value,
                                previewView = previewView
                            )
                        }
                    }, modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.nav_homed),
                        contentDescription = "",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    } else {
        LaunchedEffect(Unit) {
            permissionState.launchMultiplePermissionRequest()
        }
    }
}