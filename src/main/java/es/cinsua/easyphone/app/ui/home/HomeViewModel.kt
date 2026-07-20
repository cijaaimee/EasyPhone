package es.cinsua.easyphone.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import es.cinsua.easyphone.app.easyPreferences
import es.cinsua.easyphone.app.lock.AppLockManager
import es.cinsua.easyphone.app.ui.CallType
import es.cinsua.easyphone.app.ui.fetchRecentCalls
import es.cinsua.easyphone.app.ui.observeCallLog
import es.cinsua.easyphone.app.ui.telecom.log.CallLogViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class HomeViewModel(application: Application) : AndroidViewModel(application) {

  val locked = AppLockManager.isLocked

  private val _missedCalls = MutableStateFlow(0)
  val missedCalls: StateFlow<Int> = _missedCalls.asStateFlow()

  init {
    observeCallLog()
  }

  private fun observeCallLog() = viewModelScope.launch {
    _missedCalls.value = 0
    getApplication<Application>().observeCallLog().collectLatest {
      val lastOpenedMs = application.easyPreferences.getLong(CallLogViewModel.LAST_OPENED_KEY, 0L)
      _missedCalls.value = countNewMissedCalls(lastOpenedMs)
    }
  }

  private suspend fun countNewMissedCalls(lastCheckedMs: Long): Int {
    val cutOffInstant = if (lastCheckedMs == 0L) {
      Instant.now().minus(Duration.ofDays(30))
    } else {
      Instant.ofEpochMilli(lastCheckedMs)
    }
    val logs = fetchRecentCalls(cutOffInstant)

    val lastCheckedTime = Instant.ofEpochMilli(lastCheckedMs)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    val numbersCalledBack = mutableSetOf<String>()
    var missedCount = 0

    for (entry in logs) {
      if (entry.type == CallType.OUTGOING) {
        numbersCalledBack.add(entry.number)
      } else if (entry.type == CallType.INCOMING_MISSED) {
        val isNewerThanCheck = entry.timestamp.isAfter(lastCheckedTime)
        val hasReplied = numbersCalledBack.contains(entry.number)

        if (isNewerThanCheck && !hasReplied) {
          missedCount++
        }
      }
    }

    return missedCount
  }

  fun unlock() {
    AppLockManager.unlockApp()
  }
}
