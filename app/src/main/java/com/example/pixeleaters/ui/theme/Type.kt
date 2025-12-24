package com.example.pixeleaters.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val RobotoFamily = FontFamily.SansSerif

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = RobotoFamily),
    bodySmall = TextStyle(fontFamily = RobotoFamily),
    titleLarge = TextStyle(fontFamily = RobotoFamily),
    titleMedium = TextStyle(fontFamily = RobotoFamily),
    titleSmall = TextStyle(fontFamily = RobotoFamily),
    labelLarge = TextStyle(fontFamily = RobotoFamily),
    labelMedium = TextStyle(fontFamily = RobotoFamily),
    labelSmall = TextStyle(fontFamily = RobotoFamily)
)