package com.bitat.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.R
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.dto.common.TagsDto
import com.bitat.repository.dto.resp.BlogTagDto
import com.bitat.ui.theme.Typography
import com.bitat.utils.PublishUtils.TAG_REGEX
import com.bitat.utils.PublishUtils.TAG_REGEX_SPLIT
import kotlinx.serialization.json.Json

@Composable
fun CollapseText(value: String, maxLines: Int, modifier: Modifier = Modifier, textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(), maxLength: Int = 24, onCollapse: (Boolean) -> Unit = {}) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            text = value,
            modifier = modifier,
            maxLines = if (expanded) Int.MAX_VALUE else maxLines, // Maximum number of lines to display
            overflow = TextOverflow.Ellipsis,
            style = textStyle,
        )

        if (value.length > maxLines * maxLength) {
            Box(modifier = Modifier.background(Color.Transparent)
                .padding(top = 5.dp) // Background color
                .clickable {
                    expanded = !expanded
                    onCollapse(expanded)
                }) {

                Text(text = if (!expanded) "展开" else "收起",
                    style = textStyle.copy(color = colorResource(id = R.color.search_border)))
            }
        }
    }
}


/**
 * 显示话题的文本内容
 * */
@Composable
@Suppress("Deprecation")
fun CollapseReachText(value: String, tags: List<BlogTagDto>,maxLines: Int, modifier: Modifier = Modifier, textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(), maxLength: Int = 24, onCollapse: (Boolean) -> Unit = {}) {

   var regexAar= value.split(TAG_REGEX_SPLIT.toRegex())
//    var regResultArr = value.replace(TAG_REGEX_SPLIT.toRegex()) {
//        val dto = Json.decodeFromString(BlogTagDto.serializer(), it.value)
//        println(dto.name)
//        it.toString()
//    }

    val annotatedString = buildAnnotatedString { //创建富文本内容
        withStyle(style = SpanStyle(color = Color.Black)) {
            append("跳转到官网:")
        }
        pushStringAnnotation(tag = "juejin", annotation = "https://juejin.cn/")
        withStyle(style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.W900)) {
            append("#juejin")
        }
        pop()
        append("\n")
        withStyle(style = SpanStyle(color = Color.Black)) {
            append("跳转到官网:")
        }
        pushStringAnnotation(tag = "baidu", annotation = "https://www.baidu.com/")
        withStyle(style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.W900)) {
            append("#baidu")
        }
        pop()
    } //可点击的文本控件

    ClickableText(text = annotatedString, onClick = { offset ->
        annotatedString.getStringAnnotations(tag = "juejin", start = offset, end = offset)
            .firstOrNull()?.let { annotation -> // 点击事件的处理
                CuLog.error(CuTag.Blog,"点击了文本${annotation.item}")
            }
        annotatedString.getStringAnnotations(tag = "baidu", start = offset, end = offset)
            .firstOrNull()?.let { annotation -> // 点击事件的处理
                CuLog.error(CuTag.Blog,"点击了文本${annotation.item}")
            }
    })
}