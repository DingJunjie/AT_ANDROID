package com.bitat

import android.app.Application
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.bitat.ext.flowbus.FlowBusInitializer
import com.bitat.repository.sqlDB.SingleMsgDB
import com.bitat.repository.sqlDB.SingleRoomDB
import com.bitat.repository.sqlDB.SqlDB
import kotlinx.coroutines.Dispatchers


class BitApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this).crossfade(true) //指定内存缓存策略
            .memoryCache {
                MemoryCache.Builder(this) //指定最大Size为当前可用内存的25%
//                    .maxSizePercent(0.25) //启用或停用对缓存资源的弱引用
                    //                    .weakReferencesEnabled(true)
                    //启用或停用对缓存资源的强引用
                    //                    .strongReferencesEnabled(true)
                    //指定最大Size为50MB
                                        .maxSizeBytes(1024 * 1024 * 100)
                    .build()
            } //指定磁盘缓存策略
            .diskCache {
                DiskCache.Builder() //指定磁盘缓存的路径，没有默认值，必填。
                    .directory(this.cacheDir.resolve("image_cache")) //指定最大Size为当前可用磁盘空间的2%
                    .maxSizePercent(0.02) //指定清理逻辑执行的协程Dispatcher
                    .cleanupDispatcher(Dispatchers.IO) //指定最大Size为100MB
                    .maxSizeBytes(1024 * 1024 * 10).build()
            }.build()
    }

    override fun onCreate() {
        super.onCreate()

        SqlDB.init(this)
        //全局ctx初始化
        Local.ctx = this
        //事件发布初始化
        FlowBusInitializer.init(this)
        // emoji 初始化
        val config: EmojiCompat.Config = BundledEmojiCompatConfig(this)
        EmojiCompat.init(config)


    }


}