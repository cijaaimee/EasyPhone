package es.cinsua.easyphone.app.ui.telecom.log

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.cinsua.easyphone.app.easyPreferences
import es.cinsua.easyphone.app.ui.CallType
import es.cinsua.easyphone.app.ui.fetchRecentCalls
import es.cinsua.easyphone.app.ui.observeCallLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

data class CallLogEntry(val number: String, val name: String?, val type: CallType, val duration: Duration, val timestamp: LocalDateTime) {

  fun isNonReturnedMissedCall(calls: List<CallLogEntry>, idx: Int) =
      type == CallType.INCOMING_MISSED &&
          calls.subList(0, idx).none { it.type == CallType.OUTGOING && it.number == number }
}

data class CallLogUiState(val entries: List<CallLogEntry> = emptyList(), val isLoading: Boolean = true)

class CallLogViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(CallLogUiState())
  val uiState = _uiState.asStateFlow()

  init {
    application.easyPreferences.edit { putLong(LAST_OPENED_KEY, System.currentTimeMillis()) }
    startObservingCallLogs()
  }

  private fun startObservingCallLogs() =
      viewModelScope.launch(Dispatchers.IO) {
        getApplication<Application>().observeCallLog().collectLatest {
          _uiState.update { it.copy(isLoading = true) }
          val sevenDaysAgo = Instant.now().minus(Duration.ofDays(7))
          val recentCalls = fetchRecentCalls(sevenDaysAgo)
          _uiState.update { it.copy(entries = recentCalls, isLoading = false) }
        }
      }

  companion object {
    const val LAST_OPENED_KEY = "call_log_last_opened"
  }
}
