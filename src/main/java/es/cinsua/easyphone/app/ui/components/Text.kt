package es.cinsua.easyphone.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun FullScreenText(text: String, style: TextStyle = MaterialTheme.typography.titleLarge) {
  Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Spacer(Modifier.weight(1.0f))
    Text(text = text, textAlign = TextAlign.Center, style = style)
    Spacer(Modifier.weight(1.0f))
  }
}
