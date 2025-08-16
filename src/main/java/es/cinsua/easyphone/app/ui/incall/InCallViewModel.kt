package es.cinsua.easyphone.app.ui.incall

import android.app.Application
import android.content.Context.POWER_SERVICE
import android.os.PowerManager
import android.os.PowerManager.RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY
import android.telecom.Call
import android.telecom.CallAudioState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.cinsua.easyphone.app.contacts.ContactInfo
import es.cinsua.easyphone.app.contacts.ContactRepository
import es.cinsua.easyphone.app.telecom.InCallManager
import es.cinsua.easyphone.app.tts.TtsEngine
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class InCallUiState(
    val callState: Int = Call.STATE_DISCONNECTED,
    val callerContact: ContactInfo? = null,
    val callerNumber: String = "",
    val isProximityWakeLockManagingScreen: Boolean = false,
    val isProximitySensorNear: Boolean? = null,
) {
  val isDisconnectedOrAboutTo
    get() = callState in arrayOf(Call.STATE_DISCONNECTING, Call.STATE_DISCONNECTED)

  companion object {
    @JvmStatic val DISCONNECTED = InCallUiState()
  }
}

class InCallViewModel(
    application: Application,
    private val proximitySensorManager: ProximitySensorManager
) : ViewModel() {

  private val tts = TtsEngine(application)

  private val powerManager = application.getSystemService(POWER_SERVICE) as PowerManager
  private var proximityWakeLock: PowerManager.WakeLock? = null

  private val _uiState = MutableStateFlow(InCallUiState(callState = Call.STATE_NEW))
  val uiState: StateFlow<InCallUiState> = _uiState.asStateFlow()

  private var contactLookupJob: Job? = null
  private var lastPhoneNumber: String? = null

  init {
    if (proximitySensorManager.isSensorAvailable()) {
      proximityWakeLock =
          powerManager.newWakeLock(
              PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
              "EasyPhone:InCall:ProximityWakeLock",
          )
    }

    viewModelScope.launch {
      InCallManager.currentCall.collect { call ->
        if (call == null) {
          contactLookupJob?.cancel()
          lastPhoneNumber = null
          _uiState.value = InCallUiState.DISCONNECTED
        } else {
          val currentPhoneNumber = call.number
          val callerNumberChanged = lastPhoneNumber != currentPhoneNumber
          lastPhoneNumber = currentPhoneNumber

          _uiState.update {
            it.copy(
                callState = call.state,
                callerContact = if (callerNumberChanged) null else it.callerContact,
                callerNumber = currentPhoneNumber.ifBlank { it.callerNumber },
            )
          }

          if (callerNumberChanged) {
            contactLookupJob?.cancel()
            if (currentPhoneNumber.isNotBlank()) {
              contactLookupJob =
                  viewModelScope.launch(Dispatchers.IO) {
                    Log.d(
                        "InCallViewModel",
                        "Requesting contact info for phone number $currentPhoneNumber")
                    val contactInfo =
                        try {
                          ContactRepository.getByPhoneNumber(
                              application.applicationContext,
                              currentPhoneNumber,
                          )
                        } catch (e: CancellationException) {
                          throw e
                        } catch (e: Exception) {
                          Log.e(
                              "InCallViewModel",
                              "Error looking up contact for $currentPhoneNumber",
                              e)
                          null
                        }
                    Log.d(
                        "InCallViewModel",
                        "Contact info for phone number $currentPhoneNumber: $contactInfo")
                    if (isActive) {
                      withContext(Dispatchers.Main) {
                        _uiState.update {
                          Log.d("InCallViewModel", "Updating current call contact info")
                          it.copy(callerContact = contactInfo)
                        }
                      }
                    }
                  }
            }
          }
        }
        evaluateProximityControls()
      }
    }

    viewModelScope.launch {
      proximitySensorManager.isNear.collect { isNear ->
        _uiState.update { it.copy(isProximitySensorNear = isNear) }
        if (isNear == true) {
          Log.i("InCallViewModel", "Disabling speakerphone")
          InCallManager.setCallAudioRoute(CallAudioState.ROUTE_EARPIECE)
        } else {
          Log.i("InCallViewModel", "Enabling speakerphone")
          InCallManager.setCallAudioRoute(CallAudioState.ROUTE_SPEAKER)
        }
      }
    }
  }

  fun onUiStart() {
    Log.i("InCallViewModel", "UI started")
    InCallManager.onUiStart()
  }

  fun onUiHide() {
    Log.i("InCallViewModel", "UI hidden")
    InCallManager.onUiHide()
  }

  fun onUiDestroy() {
    Log.i("InCallViewModel", "UI destroyed")
    InCallManager.onUiDestroy()
  }

  fun answerCall() {
    InCallManager.answerCall()
  }

  fun disconnectCall() {
    InCallManager.disconnectCall()
    tts.speak("Llamada colgada") // TODO wait until TTS is done before closing UI
  }

  private fun evaluateProximityControls() {
    val state = _uiState.value
    val shouldProximityControlScreen =
        proximitySensorManager.isSensorAvailable() && !state.isDisconnectedOrAboutTo

    if (shouldProximityControlScreen) {
      registerListenerAndAcquireWakeLock()
    } else if (state.isProximityWakeLockManagingScreen || proximityWakeLock?.isHeld == true) {
      unregisterListenerAndReleaseWakeLock()
    }
  }

  private fun registerListenerAndAcquireWakeLock() {
    proximitySensorManager.startListening()
    if (proximityWakeLock?.isHeld == false) {
      proximityWakeLock?.acquire(TimeUnit.HOURS.toMillis(12))
      Log.d("InCallViewModel", "Proximity WL ACQUIRED")
    }

    val state = _uiState.value
    if (!state.isProximityWakeLockManagingScreen) {
      _uiState.update { it.copy(isProximityWakeLockManagingScreen = true) }
    }
  }

  private fun unregisterListenerAndReleaseWakeLock() {
    val state = _uiState.value

    proximitySensorManager.stopListening()
    if (proximityWakeLock?.isHeld == true) {
      proximityWakeLock?.release(
          if (state.isDisconnectedOrAboutTo) RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY else 0,
      )
      Log.d("InCallViewModel", "Proximity WL RELEASED")
    }
    _uiState.update { it.copy(isProximityWakeLockManagingScreen = false) }
  }

  override fun onCleared() {
    super.onCleared()
    contactLookupJob?.cancel()
    unregisterListenerAndReleaseWakeLock()
    tts.shutdown()
  }
}
