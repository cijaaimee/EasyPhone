package es.cinsua.easyphone.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

@Composable
fun GrayscaleImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    grayscalePercent: Float // A value from 0.0f to 1.0f
) {
  val grayscaleMatrix = ColorMatrix().apply { setToSaturation(0f) }

  val semantics =
      if (contentDescription != null) {
        Modifier.semantics {
          this.contentDescription = contentDescription
          this.role = Role.Image
        }
      } else {
        Modifier
      }

  Canvas(modifier.then(semantics)) {
    // Draw the original, full-color image
    with(painter) {
      draw(size = this@Canvas.size)

      // Calculate the height of the grayscale portion
      val grayscaleHeight = this@Canvas.size.height * grayscalePercent.coerceIn(0f, 1f)

      // Clip the drawing area to the top portion of the canvas
      if (grayscaleHeight > 0) {
        clipRect(top = 0f, left = 0f, right = this@Canvas.size.width, bottom = grayscaleHeight) {
          // Draw the image again, but with the grayscale filter applied
          draw(size = this@Canvas.size, colorFilter = ColorFilter.colorMatrix(grayscaleMatrix))
        }
      }
    }
  }
}
