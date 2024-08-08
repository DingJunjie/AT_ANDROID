package com.bitat.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers.IO


@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun RequestPermission(
    permission: String = android.Manifest.permission.CAMERA,
    tip: String = "At need camera to take some great moment",
    permissionNotAvailableContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val permissionState = rememberPermissionState(permission)
    val granted = permissionState.status.isGranted

    if (!granted) {
        LaunchedEffect(IO) {
            permissionState.launchPermissionRequest()
        }
    }

//    permissionState = permissionState,
//    permissionNotGrantedContent = {
//        Rationale(
//            text = rationale,
//            onRequestPermission = { permissionState.launchPermissionRequest() }
//        )
//    },
//    permissionNotAvailableContent = permissionNotAvailableContent,
//    content = content
//    )
}


@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun RequestPermissions(
    permission: List<String> = listOf(android.Manifest.permission.CAMERA),
    tip: String = "At need camera to take some great moment",
    permissionNotAvailableContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val permissionState = rememberMultiplePermissionsState(permission)
    if (!permissionState.allPermissionsGranted) {
        LaunchedEffect(IO) {
            permissionState.launchMultiplePermissionRequest()
        }
    }


}
