package com.example.pixeleaters.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppTopBarDefaults {
    val Height: Dp = 70.dp
    val HorizontalPadding: Dp = 16.dp
    val VerticalPadding: Dp = 11.dp
    val DividerColor: Color = Color(0xFFE6E6E6)
}

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    height: Dp = AppTopBarDefaults.Height,
    backgroundColor: Color = Color.White,
    showDivider: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(
                    horizontal = AppTopBarDefaults.HorizontalPadding,
                    vertical = AppTopBarDefaults.VerticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AppTopBarDefaults.DividerColor)
            )
        }
    }
}


