package es.cinsua.easyphone.app.telecom

import android.content.Context
import android.content.Intent
import android.telecom.Call
import android.telecom.VideoProfile
import android.util.Log
import es.cinsua.easyphone.app.ui.incall.InCallActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object InCallManager {

  private var inCallService: InCallService? = null
  private var uiState = UiState.UNBOUND

  private val _currentCall = MutableStateFlow<EasyCall?>(null)
  val currentCall = _currentCall.asStateFlow()

  private val callCallback =
      object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
          val currentCall = ensureValidCall(call)
          Log.d(
              "InCallManager",
              "Call state changed: ${currentCall.state.callStatusToString()} -> ${state.callStatusToString()}")
          _currentCall.value = currentCall.copy(state = state)
        }

        override fun onDetailsChanged(call: Call, details: Call.Details) {
          val currentCall = ensureValidCall(call)
          val newNumber = details.number
          if (currentCall.number != newNumber) {
            Log.d("InCallManager", "Call number changed: ${currentCall.number} -> $newNumber")
            _currentCall.value = currentCall.copy(number = newNumber)
          }
        }

        override fun onCallDestroyed(call: Call) {
          onCallRemoved(call)
        }

        private fun ensureValidCall(telecomCall: Call): EasyCall {
          val currentCall = _currentCall.value
          require(currentCall?.telecomCall == telecomCall) {
            "Callback for wrong call! (current: $currentCall, callback: $telecomCall)"
          }
          return currentCall
        }
      }

  fun onServiceBind(service: InCallService) {
    require(!serviceBound()) { "Cannot bind service, already bound" }
    inCallService = service
    Log.i("InCallManager", "Service bound")
  }

  fun onServiceUnbind(service: InCallService) {
    require(serviceBound()) { "Cannot unbind service, not bound" }
    require(inCallService == service) { "Cannot unbind service, not bound to the same instance" }
    require(currentCall.value == null) { "Cannot unbind service, call is not yet disconnected" }
    inCallService = null
    Log.i("InCallManager", "Service unbound")
  }

  private fun serviceBound() = inCallService != null

  fun onCallAdded(context: Context, telecomCall: Call) {
    require(serviceBound()) { "Cannot add call when service is not bound" }
    Log.i("InCallManager", "Handling new call: $telecomCall")

    val currentCall = _currentCall.value
    if (currentCall != null) {
      if (currentCall.state != Call.STATE_DISCONNECTED) {
        Log.i(
            "InCallManager",
            "Discarding secondary call $telecomCall because call is already set: ${_currentCall.value}")
        telecomCall.disconnect()
        return
      }

      Log.i("InCallManager", "Allowing new call to replace disconnected call $_currentCall")
      // Remove call without updating the state to avoid extra UI updates,
      // since we're updating it below
      onCallRemoved(currentCall.telecomCall, false)
    }

    _currentCall.value =
        EasyCall(
            telecomCall = telecomCall,
            state = telecomCall.details.state,
            number = telecomCall.details.number)
    telecomCall.registerCallback(callCallback)

    // Start the UI so the user sees the call
    startUi(context)
  }

  fun onCallRemoved(telecomCall: Call) = onCallRemoved(telecomCall, true)

  // Careful here! Setting unsetCall = false could lead to inconsistencies / leaks
  private fun onCallRemoved(telecomCall: Call, unsetCall: Boolean) {
    require(serviceBound()) { "Cannot remove call when service is not bound" }
    // This method may be called multiple times
    if (_currentCall.value?.telecomCall != telecomCall) {
      Log.d("InCallManager", "Ignoring invalid #onCallRemoved: $telecomCall")
      return
    }

    Log.i("InCallManager", "Removing call: $telecomCall")
    telecomCall.unregisterCallback(callCallback)
    if (unsetCall) {
      _currentCall.value = null
    }
  }

  private fun startUi(context: Context) {
    if (uiState == UiState.VISIBLE) {
      Log.i("InCallManager", "Skipping UI start, already bound")
      return
    }

    Log.i("InCallManager", "Starting UI")
    val intent = Intent(context, InCallActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
  }

  fun onUiStart() {
    setUiState(UiState.VISIBLE)
  }

  fun onUiHide() {
    setUiState(UiState.HIDDEN)
  }

  fun onUiDestroy() {
    setUiState(UiState.UNBOUND)
  }

  private fun setUiState(state: UiState) {
    if (uiState == state) {
      Log.d("InCallManager", "Skipping redundant UI state change: $uiState -> $state")
      return
    }

    Log.i("InCallManager", "UI state changed: $uiState -> $state")
    uiState = state
  }

  fun answerCall() {
    val call = _currentCall.value
    if (call == null || call.state != Call.STATE_RINGING) {
      return
    }
    call.telecomCall.answer(VideoProfile.STATE_AUDIO_ONLY)
  }

  fun disconnectCall() {
    val call = _currentCall.value
    if (call == null || call.isDisconnectedOrAboutTo) {
      return
    }
    if (call.state == Call.STATE_RINGING) {
      call.telecomCall.reject(Call.REJECT_REASON_DECLINED)
    } else {
      call.telecomCall.disconnect()
    }
  }

  fun setCallAudioRoute(route: Int) {
    val call = _currentCall.value
    if (call == null || call.isDisconnectedOrAboutTo) {
      return
    }
    inCallService?.setAudioRoute(route)
  }
}

private enum class UiState {
  UNBOUND,
  VISIBLE,
  HIDDEN
}
