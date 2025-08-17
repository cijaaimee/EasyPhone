package es.cinsua.easyphone.app.ui.components

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import es.cinsua.easyphone.app.ui.theme.BoxColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ButtonLayout {
  Row,
  Column
}

@Composable
fun EasyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    padding: Dp = 6.dp,
    layout: ButtonLayout = ButtonLayout.Row,
    backgroundColor: Color = BoxColor.Yellow,
    backgroundColorPressed: Color = BoxColor.DarkYellow,
    outlineColor: Color = Color.Black,
    content: @Composable () -> Unit,
) {
  val longPressDelayMillis = 250L
  val vibrationEffectMillis = 125L
  val vibrationEffectAmplitude = 255

  val context = LocalContext.current
  val vibrator = context.getSystemService(Vibrator::class.java)
  val scope = rememberCoroutineScope()

  var longPressJob by remember { mutableStateOf<Job?>(null) }
  var isPressed by remember { mutableStateOf(false) }
  var hasPressedLongEnough by remember { mutableStateOf(false) }

  val currentBackgroundColor = if (isPressed) backgroundColorPressed else backgroundColor
  val paddingValues = PaddingValues(padding)

  EasyBox(
      backgroundColor = currentBackgroundColor,
      outlineColor = outlineColor,
      modifier =
          modifier.pointerInput(Unit) {
            awaitEachGesture {
              awaitFirstDown(requireUnconsumed = false)
              isPressed = true
              hasPressedLongEnough = false

              longPressJob =
                  scope.launch {
                    delay(longPressDelayMillis)
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            vibrationEffectMillis, vibrationEffectAmplitude))
                    hasPressedLongEnough = true
                  }

              waitForUpOrCancellation()
              isPressed = false

              if (longPressJob?.isActive == true) {
                longPressJob?.cancel()
              } else if (hasPressedLongEnough) {
                hasPressedLongEnough = false
                onClick()
              }
            }
          }) {
        if (layout == ButtonLayout.Row) {
          Row(
              Modifier.padding(paddingValues),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically) {
                content()
              }
        } else {
          Column(
              Modifier.padding(paddingValues),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center) {
                content()
              }
        }
      }
}

object IconTextButton {
  enum class IconPosition {
    Top,
    Bottom,
    Start,
    End;

    fun isVertical() = this == Top || this == Bottom

    fun isBeforeText() = this == Top || this == Start

    fun isAfterText() = this == Bottom || this == End
  }
}

@Composable
fun IconTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    padding: Dp = 6.dp,
    iconPainter: Painter,
    iconSize: Dp = 24.dp,
    iconPosition: IconTextButton.IconPosition = IconTextButton.IconPosition.Start,
    text: String) {
  EasyButton(
      onClick = onClick,
      modifier = modifier,
      padding = padding,
      layout = if (iconPosition.isVertical()) ButtonLayout.Column else ButtonLayout.Row
  ) {
    if (iconPosition.isBeforeText()) {
      Image(
          painter = iconPainter,
          contentDescription = text,
          modifier = Modifier.size(iconSize),
          contentScale = ContentScale.Fit
      )
    }
    Text(
        text = text,
        textAlign = TextAlign.Center
    )
    if (iconPosition.isAfterText()) {
      Image(
          painter = iconPainter,
          contentDescription = text,
          modifier = Modifier.size(iconSize),
          contentScale = ContentScale.Fit
      )
    }
  }
}
