package es.cinsua.easyphone.app.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import es.cinsua.easyphone.app.R
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun DigitalClock(
    modifier: Modifier = Modifier,
    timeStyle: TextStyle = MaterialTheme.typography.displayLarge,
    dateStyle: TextStyle = MaterialTheme.typography.titleMedium
) {
  val timeFormat = stringResource(R.string.home_time_format)
  val dateFormat = stringResource(R.string.home_date_format)
  val timeFormatter = remember { DateTimeFormatter.ofPattern(timeFormat) }
  val dateFormatter = remember { DateTimeFormatter.ofPattern(dateFormat) }

  val now = LocalDateTime.now()
  var currentTime by remember { mutableStateOf(timeFormatter.format(now)) }
  var currentDate by remember { mutableStateOf(dateFormatter.format(now)) }

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
