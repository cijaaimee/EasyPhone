package es.cinsua.easyphone.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object EasyIcons {

  val Apps: ImageVector
    get() {
      if (_Apps != null) return _Apps!!

      _Apps =
          ImageVector.Builder(
                  name = "Apps",
                  defaultWidth = 24.dp,
                  defaultHeight = 24.dp,
                  viewportWidth = 960f,
                  viewportHeight = 960f)
              .apply {
                path(fill = SolidColor(Color(0xFF000000))) {
                  moveTo(240f, 800f)
                  quadToRelative(-33f, 0f, -56.5f, -23.5f)
                  reflectiveQuadTo(160f, 720f)
                  reflectiveQuadToRelative(23.5f, -56.5f)
                  reflectiveQuadTo(240f, 640f)
                  reflectiveQuadToRelative(56.5f, 23.5f)
                  reflectiveQuadTo(320f, 720f)
                  reflectiveQuadToRelative(-23.5f, 56.5f)
                  reflectiveQuadTo(240f, 800f)
                  moveToRelative(240f, 0f)
                  quadToRelative(-33f, 0f, -56.5f, -23.5f)
                  reflectiveQuadTo(400f, 720f)
                  reflectiveQuadToRelative(23.5f, -56.5f)
                  reflectiveQuadTo(480f, 640f)
                  reflectiveQuadToRelative(56.5f, 23.5f)
                  reflectiveQuadTo(560f, 720f)
                  reflectiveQuadToRelative(-23.5f, 56.5f)
                  reflectiveQuadTo(480f, 800f)
                  moveToRelative(240f, 0f)
                  quadToRelative(-33f, 0f, -56.5f, -23.5f)
                  reflectiveQuadTo(640f, 720f)
                  reflectiveQuadToRelative(23.5f, -56.5f)
                  reflectiveQuadTo(720f, 640f)
                  reflectiveQuadToRelative(56.5f, 23.5f)
                  reflectiveQuadTo(800f, 720f)
                  reflectiveQuadToRelative(-23.5f, 56.5f)
                  reflectiveQuadTo(720f, 800f)
                  moveTo(240f, 560f)
                  quadToRelative(-33f, 0f, -56.5f, -23.5f)
                  reflectiveQuadTo(160f, 480f)
                  reflectiveQuadToRelative(23.5f, -56.5f)
                  reflectiveQuadTo(240f, 400f)
                  reflectiveQuadToRelative(56.5f, 23.5f)
                  reflectiveQuadTo(320f, 480f)
                  reflectiveQuadToRelative(-23.5f, 56.5f)
                  reflectiveQuadTo(240f, 560f)
                  moveToRelative(240f, 0f)
                  quadToRelative(-33f, 0f, -56.5f, -23.5f)
                  reflectiveQuadTo(400f, 480f)
                  reflectiveQuadToRelative(23.5f, -56.5f)
                  reflectiveQuadTo(480f, 400f)
                  reflectiveQuadToRelative(56.5f, 23.5f)
                  reflectiveQuadTo(560f, 480f)
                  reflectiveQuadToRelative(-23.5f, 56.5f)
                  reflectiveQuadTo(480f, 560f)
                  moveToRelative(240f, 0f)
                  quadToRelative(-33f, 0f, -56.5f, -23.5f)
                  reflectiveQuadTo(640f, 480f)
                  reflectiveQuadToRelative(23.5f, -56.5f)
                  reflectiveQuadTo(720f, 400f)
                  reflectiveQuadToRelative(56.5f, 23.5f)
                  reflectiveQuadTo(800f, 480f)
                  reflectiveQuadToRelative(-23.5f, 56.5f)
                  reflectiveQuadTo(720f, 560f)
                  moveTo(240f, 320f)
                  quadToRelative(-33f, 0f, -56.5f, -23.5f)
                  reflectiveQuadTo(160f, 240f)
                  reflectiveQuadToRelative(23.5f, -56.5f)
                  reflectiveQuadTo(240f, 160f)
                  reflectiveQuadToRelative(56.5f, 23.5f)
                  reflectiveQuadTo(320f, 240f)
                  reflectiveQuadToRelative(-23.5f, 56.5f)
                  reflectiveQuadTo(240f, 320f)
                  moveToRelative(240f, 0f)
                  quadToRelative(-33f, 0f, -56.5f, -23.5f)
                  reflectiveQuadTo(400f, 240f)
                  reflectiveQuadToRelative(23.5f, -56.5f)
                  reflectiveQuadTo(480f, 160f)
                  reflectiveQuadToRelative(56.5f, 23.5f)
                  reflectiveQuadTo(560f, 240f)
                  reflectiveQuadToRelative(-23.5f, 56.5f)
                  reflectiveQuadTo(480f, 320f)
                  moveToRelative(240f, 0f)
                  quadToRelative(-33f, 0f, -56.5f, -23.5f)
                  reflectiveQuadTo(640f, 240f)
                  reflectiveQuadToRelative(23.5f, -56.5f)
                  reflectiveQuadTo(720f, 160f)
                  reflectiveQuadToRelative(56.5f, 23.5f)
                  reflectiveQuadTo(800f, 240f)
                  reflectiveQuadToRelative(-23.5f, 56.5f)
                  reflectiveQuadTo(720f, 320f)
                }
              }
              .build()

      return _Apps!!
    }

  private var _Apps: ImageVector? = null

  val Power: ImageVector
    get() {
      if (_Power != null) return _Power!!

      _Power = ImageVector.Builder(
          name = "Power_settings_new",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 960f,
          viewportHeight = 960f
      ).apply {
        path(
            fill = SolidColor(Color(0xFF000000))
        ) {
          moveTo(440f, 520f)
          verticalLineToRelative(-400f)
          horizontalLineToRelative(80f)
          verticalLineToRelative(400f)
          close()
          moveToRelative(40f, 320f)
          quadToRelative(-74f, 0f, -139.5f, -28.5f)
          reflectiveQuadTo(226f, 734f)
          reflectiveQuadToRelative(-77.5f, -114.5f)
          reflectiveQuadTo(120f, 480f)
          quadToRelative(0f, -80f, 33f, -151f)
          reflectiveQuadToRelative(93f, -123f)
          lineToRelative(56f, 56f)
          quadToRelative(-48f, 40f, -75f, 97f)
          reflectiveQuadToRelative(-27f, 121f)
          quadToRelative(0f, 116f, 82f, 198f)
          reflectiveQuadToRelative(198f, 82f)
          quadToRelative(117f, 0f, 198.5f, -82f)
          reflectiveQuadTo(760f, 480f)
          quadToRelative(0f, -64f, -26.5f, -121f)
          reflectiveQuadTo(658f, 262f)
          lineToRelative(56f, -56f)
          quadToRelative(60f, 52f, 93f, 123f)
          reflectiveQuadToRelative(33f, 151f)
          quadToRelative(0f, 74f, -28.5f, 139.5f)
          reflectiveQuadToRelative(-77f, 114.5f)
          reflectiveQuadToRelative(-114f, 77.5f)
          reflectiveQuadTo(480f, 840f)
        }
      }.build()

      return _Power!!
    }

  private var _Power: ImageVector? = null

}
