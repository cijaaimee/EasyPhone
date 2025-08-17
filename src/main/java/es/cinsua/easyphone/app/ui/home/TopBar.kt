package es.cinsua.easyphone.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.cinsua.easyphone.app.R
import es.cinsua.easyphone.app.ui.NavRoutes
import es.cinsua.easyphone.app.ui.components.EasyBox
import es.cinsua.easyphone.app.ui.components.EasyButton
import es.cinsua.easyphone.app.ui.components.EasyIcons
import es.cinsua.easyphone.app.ui.components.GrayscaleImage
import es.cinsua.easyphone.app.ui.theme.BoxColor

@Composable
fun TopIconBar(
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit,
    viewModel: TopBarViewModel = viewModel()
) {
  EasyBox(modifier = modifier, backgroundColor = BoxColor.Yellow, padding = 0.dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      ImageButton(
          icon = R.drawable.ic_sos, description = stringResource(R.string.home_button_topbar_sos)) {
            viewModel.onEmergencyClicked()
          }

      BatteryButton { viewModel.onBatteryClicked() }

      RingerButton { viewModel.onRingerClicked() }

      OptionsButton(navigateTo)
    }
  }
}

@Composable
private fun RowScope.OptionsButton(
    navigateTo: (String) -> Unit,
    viewModel: TopBarViewModel = viewModel()
) {
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
      description = stringResource(R.string.home_button_topbar_options),
      content = {
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
          /*
          DropdownMenuItem(
              text = { Text(stringResource(R.string.home_button_topbar_settings), style = MaterialTheme.typography.bodyLarge) },
              leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(32.dp)) },
              onClick = { navigateTo(NavRoutes.SETTINGS) })
          */
          DropdownMenuItem(
              text = { Text(stringResource(R.string.home_button_topbar_contacts), style = MaterialTheme.typography.bodyLarge) },
              leadingIcon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null, modifier = Modifier.size(32.dp)) },
              onClick = { viewModel.onContactsClicked() },
          )
          DropdownMenuItem(
              text = { Text(stringResource(R.string.home_button_topbar_apps), style = MaterialTheme.typography.bodyLarge) },
              leadingIcon = { Icon(EasyIcons.Apps, contentDescription = null, modifier = Modifier.size(32.dp)) },
              onClick = { navigateTo(NavRoutes.LAUNCHER) },
          )
          DropdownMenuItem(
              text = { Text(stringResource(R.string.home_button_topbar_shutdown), style = MaterialTheme.typography.bodyLarge) },
              leadingIcon = { Icon(EasyIcons.Power, contentDescription = null, modifier = Modifier.size(32.dp)) },
              onClick = { viewModel.onShutdownClicked() },
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

  ImageButton(
      icon = icon,
      description = stringResource(R.string.home_button_topbar_ringer),
      onClick = onClick)
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

  Button(modifier, onClick) {
    GrayscaleImage(
        painter = painterResource(icon),
        contentDescription = stringResource(R.string.home_button_topbar_battery),
        modifier = Modifier.size(72.dp),
        grayscalePercent = grayscalePercent,
    )
  }
}

@Composable
private fun RowScope.ImageButton(
    modifier: Modifier = Modifier,
    icon: Int,
    description: String,
    content: @Composable BoxScope.() -> Unit = {},
    onClick: () -> Unit,
) {
  Button(modifier, onClick) {
    Image(
        painter = painterResource(icon),
        contentDescription = description,
        modifier = Modifier.size(72.dp),
    )
    content()
  }
}

@Composable
private fun RowScope.Button(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
  Box(modifier = modifier.weight(1f), contentAlignment = Alignment.Center) {
    EasyButton(
        onClick = onClick,
        backgroundColor = Color.Transparent,
        backgroundColorPressed = BoxColor.DarkYellow,
        outlineColor = Color.Transparent,
        padding = 0.dp,
    ) {
      content()
    }
  }
}
