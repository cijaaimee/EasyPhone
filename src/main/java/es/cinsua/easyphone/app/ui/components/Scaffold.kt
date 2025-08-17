package es.cinsua.easyphone.app.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.cinsua.easyphone.app.ui.theme.EasyPhoneTheme
import es.cinsua.easyphone.app.ui.toast.ToastManager

@Composable
fun EasyPhoneScaffold(content: @Composable BoxScope.() -> Unit) {
  ImmersiveMode {
    EasyPhoneTheme {
      Scaffold(contentWindowInsets = WindowInsets(0.dp), modifier = Modifier.fillMaxSize()) {
          innerPadding ->
        SafeArea(modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)) {
          content()

          val toastMessage by ToastManager.message.collectAsStateWithLifecycle()
          toastMessage?.let { t -> EasyToast(message = t, modifier = Modifier.fillMaxSize()) }
        }
      }
    }
  }
}

@Composable
private fun SafeArea(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
  val paddingValues = PaddingValues(5.dp)
  val borderSize = 32.dp
  val borderColor = MaterialTheme.colorScheme.background
  val cornerShape = RoundedCornerShape(4.dp)

  Box(modifier = modifier.fillMaxSize().background(borderColor).padding(borderSize)) {
    Surface(
        modifier = Modifier.fillMaxSize().clip(cornerShape),
        color = MaterialTheme.colorScheme.background,
    ) {
      Box(modifier = Modifier.padding(paddingValues)) { content() }
    }
  }
}

@Composable
private fun ImmersiveMode(content: @Composable () -> Unit) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    val window = (view.context as Activity).window
    val insetsController = WindowCompat.getInsetsController(window, view)

    DisposableEffect(Unit) {
      insetsController.hide(WindowInsetsCompat.Type.systemBars())
      insetsController.systemBarsBehavior =
          WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

      onDispose {
        insetsController.show(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
      }
    }
  }
  content()
}
