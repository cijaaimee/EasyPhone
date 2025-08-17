package es.cinsua.easyphone.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography =
    Typography(
        displayLarge =
            TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 72.sp,
            ),
        displayMedium =
            TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp,
            ),
        displaySmall =
            TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 52.sp,
            ),
        titleLarge =
            TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight.Medium,
            ),
        titleMedium =
            TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 40.sp,
            ),
        titleSmall =
            TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 36.sp,
            ),
        bodyLarge =
            TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            ),
    )
