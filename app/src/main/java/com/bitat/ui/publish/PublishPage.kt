package com.bitat.ui.publish

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.video.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture.withOutput
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.bitat.MainCo
import com.bitat.R
import com.bitat.ui.common.CuMType
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.MediaPicker
import com.bitat.ui.component.rememberDialogState
import com.bitat.utils.doSearchQuery

import com.bitat.viewModel.PublishViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PublishPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
//            android.Manifest.permission.READ_MEDIA_AUDIO,
//            android.Manifest.permission.READ_MEDIA_VIDEO,
//            android.Manifest.permission.READ_MEDIA_IMAGES,
        )
    )

    val vm = viewModelProvider[PublishViewModel::class]
    val mediaState by vm.mediaState.collectAsState()
    val publishCommonState by vm.commonState.collectAsState()


    var recording: Recording? = remember { null }
    val previewView: PreviewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }
    }
    val videoCapture: MutableState<VideoCapture<Recorder>?> = remember { mutableStateOf(null) }
    val recordingStarted: MutableState<Boolean> = remember { mutableStateOf(false) }

    val audioEnabled: MutableState<Boolean> = remember { mutableStateOf(false) }
    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }

//    LaunchedEffect(Unit) {
//        permissionState.launchMultiplePermissionRequest()
//    }
    val dialogState = rememberDialogState()

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    if (permissionState.allPermissionsGranted) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
            Column(modifier = Modifier.padding(top = 100.dp)) {

                ImagePicker(onSelected = {
                    vm.addPicture(it)
                }) {
                    Text("选择图片")
                }

                MediaPicker(CuMType.Video) {
                    vm.addVideo(it)
                }

                MediaPicker(CuMType.Audio) {
                    vm.addAudio(it)
                }
                IconButton(onClick = {
                    MainCo.launch {
                        vm.publishMedia {
                            dialogState.show("Upload Success")
                        }
                    }
                }) {
                    Text("发布", color = Color.White)
                }

            }
            IconButton(
                onClick = {
                    if (!recordingStarted.value) {
                        videoCapture.value?.let { videoCapture ->
                            recordingStarted.value = true
                            val mediaDir = context.externalCacheDirs.firstOrNull()?.let {
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
//                                        navController.navigate("${Route.VIDEO_PREVIEW}/$uriEncoded")
                                    }
                                }
                            }
                        }
                    } else {
                        recordingStarted.value = false
                        recording?.stop()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
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
                        audioEnabled.value = !audioEnabled.value
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 32.dp)
                ) {
                    Icon(
                        painter = painterResource(if (audioEnabled.value) R.drawable.nav_msg_fill else R.drawable.nav_search_fill),
                        contentDescription = "",
                        modifier = Modifier.size(64.dp)
                    )
                }
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
                    },
                    modifier = Modifier
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

@Composable
fun CameraPreview(
    modifier: Modifier,
    cameraProvider: ProcessCameraProvider? = null,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }
    }

    var cameraPreviewRunning by remember {
        mutableStateOf(false)
    }

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }

    val audioEnabled: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }

    val recordingStarted: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }

    var recording: Recording? = remember { null }
    val videoCapture: MutableState<VideoCapture<Recorder>?> = remember { mutableStateOf(null) }

    LaunchedEffect(previewView) {
        videoCapture.value = context.createVideoCaptureUseCase(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector.value,
            previewView = previewView
        )
    }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}


@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView
): VideoCapture<Recorder> {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val qualitySelector = QualitySelector.from(
        Quality.FHD,
        FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
    )
    val recorder = Recorder.Builder()
        .setExecutor(mainExecutor)
        .setQualitySelector(qualitySelector)
        .build()
    val videoCapture = withOutput(recorder)

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        videoCapture
    )

    return videoCapture
}


@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            mainExecutor
        )
    }
}


fun startRecordingVideo(
    context: Context,
    filenameFormat: String,
    videoCapture: VideoCapture<Recorder>,
    outputDirectory: File,
    executor: Executor,
    audioEnabled: Boolean,
    consumer: Consumer<VideoRecordEvent>
): Recording {
    val videoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".mp4"
    )

    val outputOptions = FileOutputOptions.Builder(videoFile).build()

    return videoCapture.output
        .prepareRecording(context, outputOptions)
        .apply { if (audioEnabled) withAudioEnabled() }
        .start(executor, consumer)
}