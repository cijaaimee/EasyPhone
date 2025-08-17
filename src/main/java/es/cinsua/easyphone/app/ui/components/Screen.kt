package es.cinsua.easyphone.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import es.cinsua.easyphone.app.R

@Composable
fun ClosableScreen(
    modifier: Modifier = Modifier,
    closeScreen: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
  Column(modifier.fillMaxSize()) {
    Row(Modifier.padding(start = 8.dp, bottom = 8.dp)) {
      Spacer(Modifier.weight(1.0f))
      CloseButton(onClick = closeScreen)
    }

    Box(Modifier.weight(1.0f)) { content() }
  }
}

@Composable
private fun CloseButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
  EasyButton(onClick, modifier, padding = 0.dp) {
    Image(
        painter = painterResource(R.drawable.ic_exit),
        modifier = Modifier.size(48.dp),
        contentDescription = "Salir")
  }
}
