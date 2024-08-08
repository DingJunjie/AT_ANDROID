package com.bitat.utils

import android.content.Context
import coil.util.CoilUtils.result
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItemV2
import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2
import com.amap.api.services.poisearch.PoiSearchV2.OnPoiSearchListener
import com.bitat.log.CuLog
import com.bitat.log.CuTag


/**
 * 开始进行poi搜索
 */

object GaoDeUtils {


    fun doSearchQuery(ctx: Context, result: (List<PoiItemV2>) -> Unit) {
        val keyWord = ""
        var lp: LatLonPoint? = null
        val query =
            PoiSearchV2.Query("", "", "") // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.pageSize = 20 // 设置每页最多返回多少条poiitem
        query.pageNum = 0 // 设置查第一页

        try {
            val poiSearch = PoiSearchV2(ctx, query)
            poiSearch.setOnPoiSearchListener(object : OnPoiSearchListener {
                override fun onPoiSearched(p0: PoiResultV2?, p1: Int) {
                    when (p1) {
                        1000 -> { //获取成功
                            p0.let {
                                CuLog.debug(CuTag.Profile, "获取到poi共计${it?.pois?.size}个")
                                val poiList = it?.pois?.filterNotNull()
                                poiList?.forEach { item ->
                                    CuLog.debug(CuTag.Publish, "poi信息：title=${item.title}")
                                }
                                if (!poiList.isNullOrEmpty()) {
                                    result(poiList)
                                    CuLog.debug(CuTag.Publish, "回调poi数据")
                                }
                            }
                        }
                    }
                }

                override fun onPoiItemSearched(p0: PoiItemV2?, p1: Int) {
                    p0.let {
                        CuLog.debug(CuTag.Publish, "获取到ipo${it?.title}")
                    }
                }
            })
            poiSearch.bound = PoiSearchV2.SearchBound(lp, 5000, true) //
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn() // 异步搜索
        } catch (e: AMapException) {
            e.printStackTrace()
            CuLog.error(CuTag.Publish, "获取周边ipo位置异常", e)
        }
    }

    fun getLocation(ctx: Context, result: (point: LatLonPoint, name: String) -> Unit) { //声明mlocationClient对象

        val mLocationOption = AMapLocationClientOption() //设置发起定位的模式和相关参数
        val mLocationClient = AMapLocationClient(ctx) //初始化定位参数
        mLocationClient.setLocationListener { p0 ->
            CuLog.debug(CuTag.Publish, "获取到定位信息 ${p0?.address}")
            p0?.let {
                result(LatLonPoint(it.latitude, it.longitude), it.address) //完成定位后释放对象
                mLocationClient.onDestroy()
            }
        } //设置定位模式为 高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy) //设置定位间隔,单位毫秒,默认为2000ms
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        //    mLocationOption.setInterval(2000) //设置定位参数
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //获取一次定位结果：该方法默认为false。
        mLocationOption.setOnceLocation(true)
        mLocationOption.setOnceLocationLatest(true)
        mLocationClient.setLocationOption(mLocationOption) //启动定位
        mLocationClient.startLocation();
    }


}




