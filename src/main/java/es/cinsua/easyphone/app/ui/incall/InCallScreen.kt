package es.cinsua.easyphone.app.ui.incall

import android.telecom.Call
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.cinsua.easyphone.app.theme.BoxColor
import es.cinsua.easyphone.app.ui.components.EasyBox
import es.cinsua.easyphone.app.ui.components.EasyButton

@Composable
fun InCallScreen(modifier: Modifier = Modifier, uiState: InCallUiState) {
  Column(modifier = modifier.fillMaxSize().padding(vertical = 20.dp, horizontal = 14.dp)) {
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
  EasyBox { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues).fillMaxWidth()) {
      Text(
          text = getCallTitle(uiState),
          style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Medium),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth())
      Text(
          text = uiState.callerContact?.displayName ?: uiState.callerNumber,
          style = TextStyle(fontSize = 54.sp, fontWeight = FontWeight.Medium),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth())
    }
  }
}

private fun getCallTitle(uiState: InCallUiState) =
    when (uiState.callState) {
      Call.STATE_DIALING -> "Llamando"
      Call.STATE_HOLDING -> "Llamada en espera"
      Call.STATE_RINGING -> "Llamada entrante"
      Call.STATE_DISCONNECTING,
      Call.STATE_DISCONNECTED -> "Llamada terminada"
      Call.STATE_ACTIVE -> "Llamada en curso"
      else -> "Llamada en estado desconocido: ${uiState.callState}"
    }

@Composable
private fun ActionButtons(uiState: InCallUiState, viewModel: InCallViewModel = viewModel()) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
    if (uiState.callState == Call.STATE_RINGING) {
      ActionButton(onClick = { viewModel.answerCall() }, text = "✓")
    }
    if (!uiState.isDisconnectedOrAboutTo) {
      ActionButton(onClick = { viewModel.disconnectCall() }, text = "╳")
    }
  }
}

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    text: String,
) {
  EasyButton(onClick = onClick, modifier = Modifier.size(128.dp)) { paddingValues ->
    Text(
        text = text,
        modifier = Modifier.padding(paddingValues).fillMaxSize(),
        style = TextStyle(textAlign = TextAlign.Center, fontSize = 96.sp))
  }
}

@Composable
private fun Tutorial(uiState: InCallUiState) {
  if (!uiState.isDisconnectedOrAboutTo) {
    EasyBox(backgroundColor = BoxColor.Blue, modifier = Modifier.fillMaxWidth()) { paddingValues ->
      Text(
          text = getTutorialText(uiState),
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
}

private fun getTutorialText(uiState: InCallUiState) =
    when (uiState.callState) {
      Call.STATE_RINGING -> "Pulsa ✓ para responder\nPulsa ╳ para colgar"
      else -> "Pulsa ╳ para colgar"
    }
