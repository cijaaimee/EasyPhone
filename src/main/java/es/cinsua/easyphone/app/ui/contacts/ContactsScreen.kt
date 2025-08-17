package es.cinsua.easyphone.app.ui.contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.cinsua.easyphone.app.R
import es.cinsua.easyphone.app.ui.components.ClosableScreen
import es.cinsua.easyphone.app.ui.components.EasyBox
import es.cinsua.easyphone.app.ui.components.FullScreenText
import es.cinsua.easyphone.app.ui.components.IconTextButton
import es.cinsua.easyphone.app.ui.components.TutorialBox

@Composable
fun ContactsScreen(viewModel: ContactsViewModel = viewModel(), closeScreen: () -> Unit) {
  ClosableScreen(closeScreen = closeScreen) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) {
      LoadingFragment()
    } else {
      GalleryFragment(viewModel)
    }
  }
}

@Composable
private fun LoadingFragment() {
  FullScreenText(stringResource(R.string.common_text_loading))
}

@Composable
private fun GalleryFragment(viewModel: ContactsViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  if (uiState.contacts.isEmpty()) {
    FullScreenText(stringResource(R.string.contacts_text_no_contacts))
  } else {
    Column {
      TutorialBox(stringResource(R.string.contacts_tutorial_move))
      Spacer(Modifier.height(8.dp))
      TutorialBox(stringResource(R.string.contacts_tutorial_call))
      Spacer(Modifier.weight(0.75f))

      ContactBox()
      Spacer(Modifier.height(8.dp))
      SliderArrows()
      Spacer(Modifier.weight(1.0f))
    }
  }
}

@Composable
private fun ContactBox(viewModel: ContactsViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val contact = uiState.contacts[uiState.currentIndex]

  EasyBox(padding = 16.dp) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
      Text(
          text = contact.displayName,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.titleMedium
      )
      Spacer(Modifier.width(8.dp))
      IconTextButton(
          onClick = { viewModel.onContactDial() },
          text = stringResource(R.string.contacts_button_call),
          iconPainter = rememberVectorPainter(Icons.Filled.Phone),
          iconPosition = IconTextButton.IconPosition.Top,
          iconSize = 128.dp
      )
    }
  }
}

@Composable
private fun SliderArrows(viewModel: ContactsViewModel = viewModel()) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
    IconTextButton(
        onClick = { viewModel.onPreviousContact() },
        text = stringResource(R.string.contacts_button_previous),
        iconPainter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
        iconPosition = IconTextButton.IconPosition.Start)

    Spacer(Modifier.width(12.dp))

    IconTextButton(
        onClick = { viewModel.onNextContact() },
        text = stringResource(R.string.contacts_button_next),
        iconPainter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowForward),
        iconPosition = IconTextButton.IconPosition.End)
  }
}
