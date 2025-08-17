package es.cinsua.easyphone.app.ui.incall

import android.telecom.Call
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.cinsua.easyphone.app.R
import es.cinsua.easyphone.app.ui.components.EasyBox
import es.cinsua.easyphone.app.ui.components.EasyButton
import es.cinsua.easyphone.app.ui.components.TutorialBox

@Composable
fun InCallScreen(modifier: Modifier = Modifier, uiState: InCallUiState) {
  Column(modifier = modifier.fillMaxSize()) {
    CallerId(uiState)

    Spacer(modifier = Modifier.size(8.dp))

    Tutorial(uiState)

    Spacer(modifier = Modifier.weight(2.0f))

    ActionButtons(uiState)

    Spacer(modifier = Modifier.weight(1.0f))
  }
}

@Composable
private fun CallerId(uiState: InCallUiState) {
  EasyBox {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
          text = getCallTitle(uiState),
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.Center)
      Text(
          text = uiState.callerContact?.displayName ?: uiState.callerNumber,
          style = MaterialTheme.typography.titleLarge,
          textAlign = TextAlign.Center)
    }
  }
}

@Composable
private fun getCallTitle(uiState: InCallUiState) =
    when (uiState.callState) {
      Call.STATE_DIALING -> stringResource(R.string.incall_state_dialing)
      Call.STATE_HOLDING -> stringResource(R.string.incall_state_holding)
      Call.STATE_RINGING -> stringResource(R.string.incall_state_ringing)
      Call.STATE_DISCONNECTING,
      Call.STATE_DISCONNECTED -> stringResource(R.string.incall_state_disconnected)
      Call.STATE_ACTIVE -> stringResource(R.string.incall_state_active)
      else -> stringResource(R.string.incall_state_unknown, uiState.callState)
    }

@Composable
private fun ActionButtons(uiState: InCallUiState, viewModel: InCallViewModel = viewModel()) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
    if (uiState.callState == Call.STATE_RINGING) {
      ActionButton(
          onClick = { viewModel.answerCall() },
          icon = Icons.Filled.Phone,
          description = stringResource(R.string.incall_button_pickup))
    }
    if (!uiState.isDisconnectedOrAboutTo) {
      ActionButton(
          onClick = { viewModel.disconnectCall() },
          icon = Icons.Filled.Clear,
          description = stringResource(R.string.incall_button_disconnect))
    }
  }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    description: String,
) {
  EasyButton(onClick = onClick) {
    Icon(
        modifier = modifier.size(96.dp),
        imageVector = icon,
        contentDescription = description,
    )
  }
}

@Composable
private fun Tutorial(uiState: InCallUiState) {
  val text = getTutorialText(uiState)
  if (text != null) {
    TutorialBox(text)
  }
}

@Composable
private fun getTutorialText(uiState: InCallUiState) =
    if (uiState.isDisconnectedOrAboutTo) null
    else
        when (uiState.callState) {
          Call.STATE_RINGING ->
              """
            ${stringResource(R.string.incall_tutorial_pickup)}
            ${stringResource(R.string.incall_tutorial_disconnect)}"""
                  .trimIndent()
          else -> stringResource(R.string.incall_tutorial_disconnect)
        }
