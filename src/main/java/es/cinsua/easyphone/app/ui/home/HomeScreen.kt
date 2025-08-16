package es.cinsua.easyphone.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.cinsua.easyphone.app.R
import es.cinsua.easyphone.app.theme.BoxColor
import es.cinsua.easyphone.app.ui.NavRoutes
import es.cinsua.easyphone.app.ui.components.EasyBox
import es.cinsua.easyphone.app.ui.components.EasyButton

@Composable
fun HomeScreen(navigateTo: (String) -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize().padding(vertical = 20.dp, horizontal = 14.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        TopIconBar(navigateTo = navigateTo)

        EasyBox(modifier = Modifier.fillMaxWidth()) { paddingValues ->
          DigitalClock(modifier = Modifier.padding(paddingValues))
        }

        Spacer(modifier = Modifier.height(12.dp))

        EasyBox(backgroundColor = BoxColor.Blue, modifier = Modifier.fillMaxWidth()) { paddingValues
          ->
          Text(
              text = "Pulsa el teléfono para llamar",
              style =
                  TextStyle(
                      fontSize = 24.sp,
                      fontWeight = FontWeight.Medium,
                  ),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(paddingValues),
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ActionButton(
            icon = R.drawable.ic_phone,
            description = "Teléfono",
            onClick = { navigateTo(NavRoutes.DIALER) })
      }
}

@Composable
private fun ActionButton(icon: Int, description: String, onClick: () -> Unit) {
  EasyButton(
      onClick = onClick,
      modifier = Modifier.size(176.dp),
  ) { paddingValues ->
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(paddingValues)) {
          Image(
              painter = painterResource(icon),
              contentDescription = description,
              modifier = Modifier.size(128.dp),
              contentScale = ContentScale.Fit,
          )
          Text(
              text = description,
              textAlign = TextAlign.Center,
              style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium),
              modifier = Modifier.fillMaxWidth())
        }
  }
}
