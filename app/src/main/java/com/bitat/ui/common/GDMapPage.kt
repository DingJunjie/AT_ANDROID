package com.bitat.ui.common

import android.Manifest
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.PolylineOptions
import com.bitat.R
import com.bitat.utils.GaoDeUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


/**
 *    author : shilu
 *    date   : 2024/8/5  16:20
 *    desc   : 地图页面
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GDMapPage() { // 使用 remember 确保 effect 只会在状态改变时执行
    val lifecycleOwner = LocalLifecycleOwner.current
    val ctx =
        LocalContext.current //    aMapOptionsFactory: () -> AMapOptions = { AMapOptions() //    var permission = remember { false }
    val permissionState =
        rememberMultiplePermissionsState(permissions = listOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    var mapView: MapView = remember {
        MapView(ctx, AMapOptions()).apply { //            id = R.id.map
        }
    }

    //        DisposableEffect(key) {
    //            val observer = LifecycleEventObserver { source, event ->
    //                when (event) { //根据Event执行不同生命周期的操作
    //                    Lifecycle.Event.ON_CREATE -> {
    //                        CuLog.debug(CuTag.Publish, "testLiveData:DisposableEffect ON_CREATE")
    //
    //                    }
    //                    Lifecycle.Event.ON_RESUME -> {
    //                        CuLog.debug(CuTag.Publish, "testLiveData:DisposableEffect ON_RESUME")
    //                    }
    //                    Lifecycle.Event.ON_PAUSE -> {
    //                        CuLog.debug(CuTag.Publish, "testLiveData:DisposableEffect ON_PAUSE")
    //                    }
    //                    Lifecycle.Event.ON_DESTROY -> {
    //                        CuLog.debug(CuTag.Publish, "testLiveData:DisposableEffect ON_DESTROY")
    //                    }
    //                    else -> {}
    //                }
    //            }
    //            lifecycleOwner.lifecycle.addObserver(observer)
    //
    //            onDispose {
    //                CuLog.debug(CuTag.Publish, "testLiveData:DisposableEffect onDispose ")
    //                lifecycleOwner.lifecycle.removeObserver(observer)
    //
    //            }
    //
    //
    //        }


    if (permissionState.allPermissionsGranted) {
        GaoDeUtils.getLocation(ctx) { point, name ->

            //绘制标记点
        }
        AndroidView(modifier = Modifier.fillMaxSize(), factory = {
            mapView
        }, update = { //            it.onCreate()

        })
        MapLifecycle(mapView, onCreate = {
            mapView.onCreate(Bundle())
            val aMap = mapView.map

            //绘制marker
            val marker: Marker =
                aMap.addMarker(MarkerOptions().position(LatLng(39.986919, 116.353369))
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ctx.resources,
                        R.drawable.nav_add))).draggable(true))

            aMap.addPolyline(PolylineOptions().add(LatLng(43.828, 87.621), LatLng(45.808, 126.55))
                .geodesic(true).color(R.color.purple_200))


        }, // 从mapView.lifecycleObserver回调回来即可
            onResume = {
                mapView.onResume()
                mapView.map.apply {
                    setOnMapLoadedListener(null)
                    setOnCameraChangeListener(null)

                }
            }, onPause = {
                mapView.onPause()
                mapView.map.apply {
                    setOnMapLoadedListener(null)
                    setOnCameraChangeListener(null)
                }
            })
    } else {
        LaunchedEffect(Unit) {
            permissionState.launchMultiplePermissionRequest()
        }
    }


}

@Composable
fun MapLifecycle(mapView: MapView, onCreate: (() -> Unit), onResume: (() -> Unit), onPause: (() -> Unit)) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(context, lifecycle, mapView) {
        val mapLifecycleObserver = mapView.lifecycleObserver()
        val callbacks = mapView.componentCallbacks() // 添加生命周期观察者
        lifecycle.addObserver(mapLifecycleObserver) // 注册ComponentCallback
        context.registerComponentCallbacks(callbacks)
        onDispose { // 删除生命周期观察者
            mapView.onDestroy()
            lifecycle.removeObserver(mapLifecycleObserver) // 取消注册ComponentCallback
            context.unregisterComponentCallbacks(callbacks)
        }
    }
}

// 管理地图生命周期
fun MapView.lifecycleObserver(): LifecycleEventObserver = LifecycleEventObserver { _, event ->
    when (event) {
        Lifecycle.Event.ON_CREATE -> this.onCreate(Bundle())
        Lifecycle.Event.ON_RESUME -> this.onResume() // 重新绘制加载地图
        Lifecycle.Event.ON_PAUSE -> this.onPause()  // 暂停地图的绘制
        Lifecycle.Event.ON_DESTROY -> this.onDestroy() // 销毁地图
        else -> {}
    }
}

fun MapView.componentCallbacks(): ComponentCallbacks = object : ComponentCallbacks {
    // 设备配置发生改变，组件还在运行时
    override fun onConfigurationChanged(config: Configuration) {}

    // 系统运行的内存不足时，可以通过实现该方法去释放内存或不需要的资源
    override fun onLowMemory() { // 调用地图的onLowMemory
        this@componentCallbacks.onLowMemory()
    }
}