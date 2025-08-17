package es.cinsua.easyphone.app.ui.launcher

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.cinsua.easyphone.app.R
import es.cinsua.easyphone.app.ui.components.ClosableScreen
import es.cinsua.easyphone.app.ui.components.FullScreenText

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = viewModel(),
    closeScreen: () -> Unit,
) {
  ClosableScreen(closeScreen = closeScreen) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state.isLoading) {
      FullScreenText(stringResource(R.string.common_text_loading))
    } else {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = state.apps, key = { app -> app.packageName }) { app -> AppRow(app = app) }
      }
    }
  }
}

@Composable
private fun AppRow(app: AppInfo) {
  val context = LocalContext.current
  val painter = rememberDrawablePainter(drawable = app.icon)

  Row(
      modifier =
          Modifier.fillMaxWidth()
              .clickable { launchApp(context, app.packageName) }
              .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painter,
            contentDescription = app.label,
            modifier = Modifier.size(48.dp))

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = app.label, fontSize = 18.sp)
      }
}

private fun launchApp(context: Context, packageName: String) {
  val intent = context.packageManager.getLaunchIntentForPackage(packageName)
  if (intent != null) {
    context.startActivity(intent)
  }
}

@Composable
private fun rememberDrawablePainter(drawable: Drawable): Painter {
  return remember(drawable) {
    object : Painter() {
      override val intrinsicSize: Size
        get() =
            Size(
                width = drawable.intrinsicWidth.toFloat(),
                height = drawable.intrinsicHeight.toFloat())

      override fun DrawScope.onDraw() {
        drawIntoCanvas { canvas ->
          drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
          drawable.draw(canvas.nativeCanvas)
        }
      }
    }
  }
}
