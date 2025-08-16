package es.cinsua.easyphone.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import es.cinsua.easyphone.app.theme.BoxColor

@Composable
fun EasyBox(
    modifier: Modifier = Modifier,
    padding: Dp = 6.dp,
    backgroundColor: Color = BoxColor.Red,
    outlineColor: Color = Color.Black,
    outlineWidth: Dp = 1.dp,
    content: @Composable BoxScope.(PaddingValues) -> Unit
) {
  val buttonShape = RoundedCornerShape(8.dp)
  val paddingValues = PaddingValues(padding)

  Box(
      contentAlignment = Alignment.Center,
      modifier =
          modifier
              .border(width = outlineWidth, color = outlineColor, shape = buttonShape)
              .clip(buttonShape)
              .background(backgroundColor)) {
        content(paddingValues)
      }
}
