package es.cinsua.easyphone.app.ui.telecom.log

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.cinsua.easyphone.app.R
import es.cinsua.easyphone.app.ui.CallType
import es.cinsua.easyphone.app.ui.components.ClosableScreen
import es.cinsua.easyphone.app.ui.components.EasyBox
import es.cinsua.easyphone.app.ui.components.FullScreenText
import es.cinsua.easyphone.app.ui.components.TutorialBox
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CallLogScreen(
    missedOnly: Boolean,
    viewModel: CallLogViewModel = viewModel(),
    closeScreen: () -> Unit,
) {
  ClosableScreen(closeScreen = closeScreen) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state.isLoading) {
      FullScreenText(stringResource(R.string.common_text_loading))
    } else {
      Column {
        val tutorialText = if (missedOnly) {
          R.string.call_log_tutorial_missed
        } else {
          R.string.call_log_tutorial
        }
        TutorialBox(stringResource(tutorialText))

        Spacer(modifier = Modifier.height(8.dp))

        val logEntries = state.entries.filterIndexed { idx, call ->
          !missedOnly || call.isNonReturnedMissedCall(state.entries, idx)
        }
        if (logEntries.isEmpty()) {
          val text = if (missedOnly) {
            R.string.call_log_no_missed_calls
          } else {
            R.string.call_log_no_recent_calls
          }
          FullScreenText(stringResource(text))
        } else {
          LazyColumn(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(items = logEntries) { LogEntryRow(it) }
          }
        }
      }
    }
  }
}

@Composable
private fun LogEntryRow(entry: CallLogEntry) {
  EasyBox {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
      Image(
          painter = painterResource(entry.type.toIconResource()),
          contentDescription = entry.type.toDescription(),
          modifier = Modifier.size(48.dp))

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
        val displayName = entry.name?.takeIf { it.isNotBlank() } ?: formatPhoneNumber(LocalContext.current, entry.number)
        Text(text = displayName, fontSize = 32.sp)

        Spacer(modifier = Modifier.height(2.dp))

        val subtitleText = formatSubtitle(entry.timestamp, entry.duration)
        Text(
            text = subtitleText,
            fontSize = 18.sp
        )
      }
    }
  }
}

private fun formatPhoneNumber(context: Context, number: String): String {
  val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
  val countryIso = telephonyManager.networkCountryIso.takeIf { it.isNotBlank() }
    ?: telephonyManager.simCountryIso.takeIf { it.isNotBlank() }
    ?: Locale.getDefault().country

  return PhoneNumberUtils.formatNumber(number, countryIso.uppercase()) ?: number
}

@Composable
private fun formatSubtitle(dateTime: LocalDateTime, duration: Duration): String {
  val dateTimeFormat = stringResource(R.string.call_log_datetime_format)
  val dateFormatter = remember {
    DateTimeFormatter.ofPattern(dateTimeFormat)
  }
  val formattedDate = dateTime.format(dateFormatter)

  if (duration.isZero || duration.isNegative) {
    return formattedDate
  }

  val totalSeconds = duration.seconds
  val hours = totalSeconds / 3600
  val minutes = (totalSeconds % 3600) / 60
  val seconds = totalSeconds % 60

  val durationText = when {
    hours > 0 -> {
      // Formats minutes with a leading zero if needed, matching "1h03m"
      val paddedMinutes = String.format(Locale.US, "%02dm", minutes)
      "${hours}h$paddedMinutes"
    }
    minutes > 0 -> {
      "${minutes}m"
    }
    else -> {
      "${seconds}s"
    }
  }

  return "$formattedDate - $durationText"
}

private fun CallType.toIconResource() = when(this) {
  CallType.INCOMING_ANSWERED -> R.drawable.ic_call_log_call_incoming
  CallType.INCOMING_MISSED -> R.drawable.ic_call_log_call_incoming_missed
  CallType.OUTGOING -> R.drawable.ic_call_log_call_outgoing
}

private fun CallType.toDescription() = when(this) {
  CallType.INCOMING_ANSWERED -> "Llamada entrante"
  CallType.INCOMING_MISSED -> "Llamada perdida"
  CallType.OUTGOING -> "Llamada saliente"
}
