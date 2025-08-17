package es.cinsua.easyphone.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun TutorialBox(text: String) {
  EasyBox(backgroundColor = BoxColor.Blue, modifier = Modifier.fillMaxWidth()) { paddingValues ->
    Text(
        text = text,
        style =
            TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(paddingValues),
    )
  }
}
