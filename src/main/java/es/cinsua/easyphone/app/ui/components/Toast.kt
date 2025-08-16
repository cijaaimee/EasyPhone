package es.cinsua.easyphone.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EasyToast(message: String, modifier: Modifier = Modifier) {
  Box(contentAlignment = Alignment.Center, modifier = modifier) {
    Text(
        text = message,
        color = Color.White,
        fontSize = 24.sp,
        modifier =
            Modifier.background(
                    color = Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp))
  }
}
