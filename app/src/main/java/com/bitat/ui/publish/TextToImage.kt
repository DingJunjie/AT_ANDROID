package com.bitat.ui.publish

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.utils.saveBitmapToCache

/**
 *    author : shilu
 *    date   : 2024/9/5  15:51
 *    desc   :
 */
@Composable
fun TextToImage() {
    val context = LocalContext.current
    val density = LocalDensity.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            bitmap = createTextAsBitmap("Hello Compose!", 750, 1000, density)
            bitmap?.let {
            //                saveBitmapToFile(context, it, "text_image.png")
                saveBitmapToCache(context, it, "封面")
            }
        }) {
            Text("Generate Image from Text")
        }

        Spacer(modifier = Modifier.height(16.dp))

        bitmap?.let {
            Image(bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(300.dp))
        }
    }
}

// 创建文本的 Bitmap
fun createTextAsBitmap(text: String, widthPx: Int, heightPx: Int, density: Density): Bitmap {
    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // 填充背景色
    canvas.drawColor(android.graphics.Color.CYAN)
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = with(density) { 24.sp.toPx() }
        isAntiAlias = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    } // 绘制文本 // 计算文本宽度
    val textWidth = paint.measureText(text) // 计算 X 轴和 Y 轴位置，确保文本居中
    val xPos = (canvas.width / 2 - textWidth / 2).toFloat()  // X轴中间
    val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)  // Y轴中间
    canvas.drawText(text, xPos, yPos, paint)

    return bitmap
}

// 保存 Bitmap 为文件
fun saveBitmapToFile(context: android.content.Context, bitmap: Bitmap, fileName: String) { //    val filePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    //    val imageFile = File(filePath, fileName)
    //
    //    try {
    //        FileOutputStream(imageFile).use { out ->
    //            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    //        }
    //    } catch (e: Exception) {
    //        e.printStackTrace()
    //    }
}