package com.example.clinicasaludplus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
        displayLarge = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
        ),
        headlineSmall = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
        ),
        bodyLarge = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
        ),
        bodyMedium = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
        ),
        labelSmall = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
        )
)