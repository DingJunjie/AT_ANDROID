package com.bitat.ui.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.CHANNEL_ID
import com.bitat.R
import com.bitat.router.NavigationItem
import com.bitat.ui.theme.Typography
import kotlinx.serialization.json.Json
import java.security.Permissions

class SnackbarVisualsWithError(override val message: String, val isError: Boolean) :
    SnackbarVisuals {
    override val actionLabel: String
        get() = if (isError) "Error" else "OK"

    override val withDismissAction: Boolean
        get() = false

    override val duration: SnackbarDuration
        get() = SnackbarDuration.Indefinite
}

@Composable
fun SnackbarNotification(data: SnackbarData, navHostController: NavHostController) {
    val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
    val buttonColor =
        if (isError) {
            ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            )
        } else {
            ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.inversePrimary
            )
        }

    val realData = Json.decodeFromString(NotificationOps.serializer(), data.visuals.message)

    Snackbar(
        modifier =
        Modifier
//            .border(2.dp, MaterialTheme.colorScheme.secondary)
//            .padding(horizontal = 12.dp)
            .height(160.dp)
            .clickable {
                navHostController.navigate(NavigationItem.Notification.route)
                data.dismiss()
            },
        shape = RectangleShape,
        action = {
            Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                TextButton(
                    onClick = {
                        if (isError) data.dismiss() else data.performAction()
//                    data.dismiss()
                    },
                    colors = buttonColor
                ) {
                    Text(data.visuals.actionLabel ?: "")
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (realData.cover != "") Box(
                    modifier = Modifier
                        .height(160.dp)
                        .width(80.dp), contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = realData.cover,
                        contentDescription = "",
                        modifier = Modifier
                            .width(80.dp)
                            .height(120.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                        .height(160.dp), verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = realData.title,
                        style = Typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(realData.content, style = Typography.bodyMedium.copy(color = Color.White))
                }

            }
        }
    }
}

fun showNotification(context: Context, notificationId: Int) {
    // 创建通知渠道（仅在 Android 8.0 及以上版本需要）
//    val importance = NotificationManager.IMPORTANCE_DEFAULT
//    val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
//        description = "This is my channel description"
//    }
//    val notificationManager: NotificationManager =
//        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    notificationManager.createNotificationChannel(channel)

    // 创建通知内容
    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle("Notification Title")
        .setContentText("This is the notification content.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    // 添加点击通知后的行为（这里以启动一个 Activity 为例）
//    val intent = Intent(context, YourTargetActivity::class.java)
//    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
//    notificationBuilder.setContentIntent(pendingIntent)
    println("click notification")

    // 发送通知
    val notificationManagerCompat = NotificationManagerCompat.from(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.

        return
    }
    NotificationOps(
        title = "my title",
        content = "this is content",
        false,
        "",
        "https://pic3.zhimg.com/v2-9041577bc5535d6abd5ddc3932f2a30e_r.jpg",
        kind = 9,
        sourceId = 0,
        commentId = 0,
        comment = "",
        userId = 0,
    ).showNotification()
    notificationManagerCompat.notify(notificationId, notificationBuilder.build())
}