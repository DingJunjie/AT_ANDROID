package com.bitat.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.R
import com.bitat.ui.theme.Typography

@Composable
fun CollapseText(
    value: String,
    maxLines: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
    ),
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
                .padding(top = 5.dp)// Background color
                .clickable {
                    expanded = !expanded
                    onCollapse(expanded)
                }) {

                Text(
                    text = if (!expanded) "展开" else "收起",
                    style = textStyle.copy(
                        color = colorResource(id = R.color.search_border)
                    )
                )
            }
        }
    }
}