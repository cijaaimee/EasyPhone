package es.cinsua.easyphone.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.cinsua.easyphone.app.R
import es.cinsua.easyphone.app.theme.BoxColor
import es.cinsua.easyphone.app.ui.NavRoutes
import es.cinsua.easyphone.app.ui.components.EasyBox
import es.cinsua.easyphone.app.ui.components.EasyButton
import es.cinsua.easyphone.app.ui.components.EasyIcons
import es.cinsua.easyphone.app.ui.components.GrayscaleImage

@Composable
fun TopIconBar(navigateTo: (String) -> Unit, viewModel: TopBarViewModel = viewModel()) {
  EasyBox(
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
      backgroundColor = BoxColor.Yellow,
  ) { paddingValues ->
    Row(
        modifier = Modifier.fillMaxWidth().padding(paddingValues),
        horizontalArrangement = Arrangement.SpaceAround, // Espacia los iconos uniformemente
        verticalAlignment = Alignment.CenterVertically,
    ) {
      ImageButton(icon = R.drawable.ic_sos, description = "Botón de emergencia SOS") {
        viewModel.onEmergencyClicked()
      }

      BatteryButton { viewModel.onBatteryClicked() }

      RingerButton { viewModel.onRingerClicked() }

      OptionsButton(navigateTo)
    }
  }
}

@Composable
private fun RowScope.OptionsButton(navigateTo: (String) -> Unit) {
  var expanded by remember { mutableStateOf(false) }
  val lifecycleOwner = LocalLifecycleOwner.current

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_STOP) {
        expanded = false
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  ImageButton(
      icon = R.drawable.ic_settings,
      description = "Opciones",
      content = {
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
          /*
          DropdownMenuItem(
              text = { Text("Ajustes") },
              leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
              onClick = { navigateTo(NavRoutes.SETTINGS) })
          */
          DropdownMenuItem(
              text = { Text("Aplicaciones") },
              leadingIcon = { Icon(EasyIcons.Apps, contentDescription = null) },
              onClick = { navigateTo(NavRoutes.LAUNCHER) },
          )
        }
      },
      onClick = { expanded = !expanded })
}

@Composable
private fun RowScope.RingerButton(
    viewModel: TopBarViewModel = viewModel(),
    onClick: () -> Unit,
) {
  val ringerState by viewModel.ringerModeState.collectAsStateWithLifecycle()
  val icon =
      when (ringerState) {
        RingerModeState.NORMAL -> R.drawable.ic_speaker
        RingerModeState.VIBRATE,
        RingerModeState.SILENT -> R.drawable.ic_speaker_mute
      }

  ImageButton(icon = icon, description = "Control de volumen", onClick = onClick)
}

@Composable
private fun RowScope.BatteryButton(
    modifier: Modifier = Modifier,
    viewModel: TopBarViewModel = viewModel(),
    onClick: () -> Unit
) {
  val batteryLevel by viewModel.batteryLevel.collectAsStateWithLifecycle()
  val charging by viewModel.charging.collectAsStateWithLifecycle()

  val grayscalePercent = 1.0f - batteryLevel
  val icon = if (charging) R.drawable.ic_battery_charging else R.drawable.ic_battery

  Button(modifier, onClick) { paddingValues ->
    GrayscaleImage(
        painter = painterResource(icon),
        contentDescription = "Batería",
        modifier = Modifier.size(72.dp).padding(paddingValues),
        grayscalePercent = grayscalePercent,
    )
  }
}

@Composable
private fun RowScope.ImageButton(
    modifier: Modifier = Modifier,
    icon: Int,
    description: String,
    content: @Composable BoxScope.(PaddingValues) -> Unit = {},
    onClick: () -> Unit,
) {
  Button(modifier, onClick) { paddingValues ->
    Image(
        painter = painterResource(icon),
        contentDescription = description,
        modifier = Modifier.size(72.dp).padding(paddingValues),
    )

    content(paddingValues)
  }
}

@Composable
private fun RowScope.Button(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable BoxScope.(PaddingValues) -> Unit
) {
  Box(modifier = modifier.weight(1f), contentAlignment = Alignment.Center) {
    EasyButton(
        onClick = onClick,
        backgroundColor = Color.Transparent,
        backgroundColorPressed = BoxColor.DarkYellow,
        outlineColor = Color.Transparent,
    ) { paddingValues ->
      content(paddingValues)
    }
  }
}
