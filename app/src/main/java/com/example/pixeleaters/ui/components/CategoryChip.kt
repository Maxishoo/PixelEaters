package com.example.pixeleaters.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    val color = when (category) {
        "Работа" -> Color(0xFF4CAF50)
        "Учеба" -> Color(0xFF2196F3)
        "Друг" -> Color(0xFFFF9800)
        "Семья" -> Color(0xFF9C27B0)
        "Коллега" -> Color(0xFF795548)
        else -> Color(0xFF607D8B)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color,
        contentColor = Color.White
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}