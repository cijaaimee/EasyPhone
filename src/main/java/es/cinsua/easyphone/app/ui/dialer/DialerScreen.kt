package es.cinsua.easyphone.app.ui.dialer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.cinsua.easyphone.app.ui.components.ClosableScreen
import es.cinsua.easyphone.app.ui.components.EasyBox
import es.cinsua.easyphone.app.ui.components.EasyButton
import es.cinsua.easyphone.app.ui.components.TutorialBox

@Composable
fun DialerScreen(viewModel: DialerViewModel = viewModel(), closeScreen: () -> Unit) {
  ClosableScreen(closeScreen = closeScreen) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) {
      LoadingFragment()
    } else {
      GalleryFragment(viewModel)
    }
  }
}

@Composable
private fun LoadingFragment() {
  FullScreenText("Cargando...")
}

@Composable
private fun GalleryFragment(viewModel: DialerViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  if (uiState.contacts.isEmpty()) {
    FullScreenText("No hay contactos")
  } else {
    val contact = uiState.contacts[uiState.currentIndex]

    Column {
      TutorialBox("Pulsa las flechas para buscar una persona")
      Spacer(Modifier.height(8.dp))
      TutorialBox("Pulsa el teléfono para llamar")
      Spacer(Modifier.weight(1.0f))

      EasyBox {
        Text(modifier = Modifier.fillMaxWidth().padding(8.dp), text = contact.displayName, textAlign = TextAlign.Center, style = TextStyle(
            fontSize = 48.sp,
            fontWeight = FontWeight.Medium
        ))
      }
      Spacer(Modifier.weight(1.0f))

      Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.Center) {
        ActionButton(
            icon = Icons.Filled.Phone,
            description = "",
            iconPosition = ActionIconPosition.START,
            iconSize = 128.dp
        ) {
          viewModel.onContactDial()
        }
      }
      Spacer(Modifier.weight(1.0f))

      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        ActionButton(icon = Icons.AutoMirrored.Filled.ArrowBack, description = "Anterior", iconPosition = ActionIconPosition.START) {
          viewModel.onPreviousContact()
        }
        Spacer(Modifier.width(12.dp))
        ActionButton(icon = Icons.AutoMirrored.Filled.ArrowForward, description = "Siguiente", iconPosition = ActionIconPosition.END) {
          viewModel.onNextContact()
        }
      }
      Spacer(Modifier.weight(1.0f))
    }
  }
}

@Composable
private fun FullScreenText(text: String) {
  Column(modifier = Modifier.fillMaxSize()) {
    Spacer(Modifier.weight(1.0f))
    Text(modifier = Modifier.fillMaxWidth(), text = text, textAlign = TextAlign.Center, style = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.Medium
    ))
    Spacer(Modifier.weight(1.0f))
  }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    description: String,
    iconPosition: ActionIconPosition,
    iconSize: Dp = 24.dp,
    onClick: () -> Unit) {
  EasyButton(
      onClick = onClick,
      padding = 8.dp
  ) { paddingValues ->
    if (iconPosition == ActionIconPosition.TOP || iconPosition == ActionIconPosition.BOTTOM) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(paddingValues)) {
        ActionButtonContent(icon, description, iconPosition, iconSize)
      }
    } else {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(paddingValues)) {
        ActionButtonContent(icon, description, iconPosition, iconSize)
      }
    }
  }
}

@Composable
private fun ActionButtonContent(icon: ImageVector,
                                description: String,
                                iconPosition: ActionIconPosition,
                                iconSize: Dp = 24.dp) {
  if (iconPosition == ActionIconPosition.TOP || iconPosition == ActionIconPosition.START) {
    Image(
        painter = rememberVectorPainter(icon),
        contentDescription = description,
        modifier = Modifier.size(iconSize),
        contentScale = ContentScale.Fit,
    )
  }
  Text(
      text = description,
      textAlign = TextAlign.Center,
      style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium))
  if (iconPosition == ActionIconPosition.END || iconPosition == ActionIconPosition.BOTTOM) {
    Image(
        painter = rememberVectorPainter(icon),
        contentDescription = description,
        modifier = Modifier.size(iconSize),
        contentScale = ContentScale.Fit,
    )
  }
}

private enum class ActionIconPosition {
  TOP,
  BOTTOM,
  START,
  END
}
