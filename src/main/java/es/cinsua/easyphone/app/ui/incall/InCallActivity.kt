package es.cinsua.easyphone.app.ui.incall

import android.app.Application
import android.os.Bundle
import android.telecom.Call
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.cinsua.easyphone.app.ui.EasyActivity
import es.cinsua.easyphone.app.ui.components.EasyPhoneScaffold

class InCallActivity : EasyActivity() {

  private val viewModel: InCallViewModel by viewModels { InCallViewModelFactory(application) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    window.addFlags(
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

    setContent {
      EasyPhoneScaffold {
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        when (state.callState) {
          Call.STATE_DISCONNECTED ->
              finish() // TODO: what happens if the state changes during finish? restart?
          else -> InCallScreen(uiState = state)
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    viewModel.onUiStart()
  }

  override fun onStop() {
    super.onStop()
    if (!isFinishing) {
      viewModel.onUiHide()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.onUiDestroy()
  }

  override fun finish() {
    // Make sure the activity is completely removed so it doesn't appear in recent apps
    super.finishAndRemoveTask()
  }
}

private class InCallViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    val app = application
    val proximityManager = ProximitySensorManager(app)
    @Suppress("UNCHECKED_CAST")
    return InCallViewModel(app, proximityManager) as T
  }
}
