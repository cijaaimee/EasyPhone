package es.cinsua.easyphone.app.ui.components

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import es.cinsua.easyphone.app.theme.BoxColor
import es.cinsua.easyphone.app.theme.ButtonLongPressTime
import es.cinsua.easyphone.app.theme.ButtonLongPressVibrationAmplitude
import es.cinsua.easyphone.app.theme.ButtonLongPressVibrationTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EasyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    padding: Dp = 6.dp,
    backgroundColor: Color = BoxColor.Yellow,
    backgroundColorPressed: Color = BoxColor.DarkYellow,
    outlineColor: Color = Color.Black,
    longPressDelayMillis: Long = ButtonLongPressTime,
    vibrationEffectMillis: Long = ButtonLongPressVibrationTime,
    vibrationEffectAmplitude: Int = ButtonLongPressVibrationAmplitude,
    content: @Composable BoxScope.(PaddingValues) -> Unit
) {
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
        content(paddingValues)
      }
}
