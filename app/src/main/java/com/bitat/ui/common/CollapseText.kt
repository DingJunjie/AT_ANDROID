package com.bitat.ui.common

import android.text.style.ClickableSpan
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
import java.util.regex.Matcher
import java.util.regex.Pattern

@Composable
fun CollapseText(
    value: String,
    maxLines: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(),
    maxLength: Int = 24,
    onCollapse: (Boolean) -> Unit = {}
) {
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
            Box(modifier = Modifier
                .background(Color.Transparent)
                .padding(top = 5.dp) // Background color
                .clickable {
                    expanded = !expanded
                    onCollapse(expanded)
                }) {

                Text(
                    text = if (!expanded) "展开" else "收起",
                    style = textStyle.copy(color = colorResource(id = R.color.search_border))
                )
            }
        }
    }
}

fun compileTag(value: String): MutableList<String> {
    val pattern = Pattern.compile(TAG_REGEX_SPLIT)
    val matcher = pattern.matcher(value)
    val result = mutableListOf<String>()
    var start = 0
    while (matcher.find()) {
        val separator = matcher.group()
        if (start < matcher.start()) {
            result.add(value.substring(start, matcher.start()))
        }
        result.add(separator)
        start = matcher.end()
    }
    if (start < value.length) {
        result.add(value.substring(start))
    }
    return result
}

fun matchTag(value: String): BlogTagDto? { //    \\\^#\{(\d+):(\p{IsHan}+)\}\^\\
    try {
        val pattern = Pattern.compile("[0-9]+")
        val im: Matcher = pattern.matcher(value)

        val cp = Pattern.compile("""\p{IsHan}+""")
        val cm: Matcher = cp.matcher(value)

        im.find()
        cm.find()
        val idg = im.group()
        val content = cm.group()

        return BlogTagDto().apply {
            id = if (idg == "") 0 else idg.toLong()
            name = content
        }
    } catch (e: Exception) {
        return null
    }
}

/**
 * 显示话题的文本内容
 * */
@Composable
@Suppress("Deprecation")
fun CollapseReachText(
    value: String,
    tags: List<BlogTagDto>,
    maxLines: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(),
    maxLength: Int = 24,
    tagTap: (String) -> Unit,
    contentTap: () -> Unit,
    onCollapse: (Boolean) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val res = compileTag(value)

    var isClickTag = false

    val contentWithTag = buildAnnotatedString {
        if (res.size > 1) { // 带tag
            res.map { v ->
                val tag = matchTag(v)
                if (tag == null) {
                    withStyle(style = SpanStyle(color = Color.Black)) {
                        append(v)
                    }
                } else {
                    pushStringAnnotation(tag = tag.id.toString(), annotation = tag.name)
                    withStyle(style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.W500)) {
                        append("#${tag.name}")
                    }
                    pop()
                }
//                if (i % 2 == 1) {
//                    // \^#{\d+:\w+}^\
//                    val tag = matchTag(v)
//                    pushStringAnnotation(tag = tag.id.toString(), annotation = tag.name)
//                    withStyle(style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.W500)) {
//                        append("#${tag.name}")
//                    }
//                    pop()
//                } else {
//                    withStyle(style = SpanStyle(color = Color.Black)) {
//                        append(v)
//                    }
//                }
            }
        } else if (res.size == 1) {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append(res[0])
            }
        }
    }
    Column {
        ClickableText(
            text = contentWithTag,
            onClick = { offset -> //可点击的文本控件
                if (tags.isNotEmpty()) {
                    tags.forEach { tag ->
                        contentWithTag.getStringAnnotations(
                            tag = "${tag.id}",
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let { annotation -> // 点击事件的处理
                            tagTap(annotation.item)
                            isClickTag = true
                        }
                    }
                    if (!isClickTag) {
                        contentTap()
                    }
                } else {
                    contentTap()
                }
            },
            maxLines = if (expanded) Int.MAX_VALUE else maxLines, // Maximum number of lines to display
            overflow = TextOverflow.Ellipsis,
            style = textStyle,
        )

        if (value.length > maxLines * maxLength) {
            Box(modifier = Modifier
                .background(Color.Transparent)
                .padding(top = 5.dp) // Background color
                .clickable {
                    expanded = !expanded
                    onCollapse(expanded)
                }) {

                Text(
                    text = if (!expanded) "展开" else "收起",
                    style = textStyle.copy(color = colorResource(id = R.color.search_border))
                )
            }
        }
    }
}