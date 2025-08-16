package es.cinsua.easyphone.app.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

@Composable
internal fun DigitalClock(
    modifier: Modifier = Modifier,
    timeStyle: TextStyle = TextStyle(fontSize = 72.sp, fontWeight = FontWeight.Bold),
    dateStyle: TextStyle = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Medium)
) {
  var currentTime by remember { mutableStateOf("") }
  var currentDate by remember { mutableStateOf("") }

  val timeFormatter = remember { DateTimeFormatter.ofPattern("hh:mm") }
  val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy") }

  LaunchedEffect(key1 = true) {
    while (true) {
      val now = LocalDateTime.now()
      currentTime = timeFormatter.format(now)
      currentDate = dateFormatter.format(now)
      delay(1000L)
    }
  }
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = currentTime, style = timeStyle, textAlign = TextAlign.Center)
    Text(
        text =
            currentDate.replaceFirstChar {
              if (it.isLowerCase()) it.titlecase() else it.toString()
            },
        style = dateStyle,
        textAlign = TextAlign.Center)
  }
}
