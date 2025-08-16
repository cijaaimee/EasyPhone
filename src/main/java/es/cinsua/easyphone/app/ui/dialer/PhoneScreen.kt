package es.cinsua.easyphone.app.ui.dialer

import androidx.compose.runtime.Composable
import es.cinsua.easyphone.app.ui.components.ClosableScreen

@Composable
fun PhoneScreen(closeScreen: () -> Unit) {
  ClosableScreen(closeScreen = closeScreen) {}
}
