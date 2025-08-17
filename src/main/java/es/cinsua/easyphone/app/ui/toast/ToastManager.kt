package es.cinsua.easyphone.app.ui.toast

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ToastManager {

  private val _message = MutableStateFlow<String?>(null)
  val message = _message.asStateFlow()

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private var toastJob: Job? = null

  fun show(message: String) {
    toastJob?.cancel()
    toastJob =
        scope.launch {
          _message.value = message
          delay(2500L)
          _message.value = null
        }
  }
}
