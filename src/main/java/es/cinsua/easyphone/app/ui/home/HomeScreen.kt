package es.cinsua.easyphone.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.cinsua.easyphone.app.R
import es.cinsua.easyphone.app.ui.NavRoutes
import es.cinsua.easyphone.app.ui.components.ButtonLayout
import es.cinsua.easyphone.app.ui.components.EasyBox
import es.cinsua.easyphone.app.ui.components.EasyButton
import es.cinsua.easyphone.app.ui.components.TutorialBox

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(), navigateTo: (String) -> Unit) {
  val locked by viewModel.locked.collectAsStateWithLifecycle()
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    TopIconBar(modifier = Modifier.alpha(if (locked) 0.0f else 1.0f), navigateTo = navigateTo)
    Spacer(modifier = Modifier.height(8.dp))

    EasyBox(modifier = Modifier.fillMaxWidth()) { DigitalClock() }

    Spacer(modifier = Modifier.height(12.dp))

    Tutorial(viewModel)

    Spacer(modifier = Modifier.height(12.dp))

    ActionButtons(navigateTo)
  }
}

@Composable
private fun ActionButtons(navigateTo: (String) -> Unit, viewModel: HomeViewModel = viewModel()) {
  val locked by viewModel.locked.collectAsStateWithLifecycle()
  if (locked) {
    IconTextButton(
        painter = painterResource(R.drawable.ic_lock),
        text = stringResource(R.string.home_button_unlock),
        onClick = { viewModel.unlock() })
  } else {
    IconTextButton(
        painter = painterResource(R.drawable.ic_contacts),
        text = stringResource(R.string.home_button_contacts),
        onClick = { navigateTo(NavRoutes.CONTACTS) })
  }
}

@Composable
private fun IconTextButton(painter: Painter, text: String, onClick: () -> Unit) {
  EasyButton(onClick = onClick, layout = ButtonLayout.Column) {
    Image(
        painter = painter,
        contentDescription = text,
        modifier = Modifier.size(128.dp),
        contentScale = ContentScale.Fit,
    )
    Text(text = text, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
  }
}

@Composable
private fun Tutorial(viewModel: HomeViewModel = viewModel()) {
  val locked by viewModel.locked.collectAsStateWithLifecycle()
  TutorialBox(text = stringResource(getTutorialText(locked)))
}

private fun getTutorialText(locked: Boolean) =
    if (locked) {
      R.string.home_tutorial_unlock
    } else {
      R.string.home_tutorial_call
    }
