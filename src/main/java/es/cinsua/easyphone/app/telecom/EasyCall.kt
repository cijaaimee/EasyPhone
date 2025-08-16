package es.cinsua.easyphone.app.telecom

import android.telecom.Call

data class EasyCall(internal val telecomCall: Call, val state: Int, val number: String) {

  val isDisconnectedOrAboutTo
    get() = state in arrayOf(Call.STATE_DISCONNECTING, Call.STATE_DISCONNECTED)

  override fun toString(): String {
    return "EasyCall{number=$number,state=${state.callStatusToString()}}"
  }
}

val Call.Details.number
  get() = handle.schemeSpecificPart ?: ""

fun Int.callStatusToString(): String {
  return when (this) {
    Call.STATE_NEW -> "NEW"
    Call.STATE_DIALING -> "DIALING"
    Call.STATE_RINGING -> "RINGING"
    Call.STATE_HOLDING -> "ON_HOLD"
    Call.STATE_ACTIVE -> "ACTIVE"
    Call.STATE_DISCONNECTED -> "DISCONNECTED"
    Call.STATE_SELECT_PHONE_ACCOUNT -> "SELECT_PHONE_ACCOUNT"
    Call.STATE_CONNECTING -> "CONNECTING"
    Call.STATE_DISCONNECTING -> "DISCONNECTING"
    Call.STATE_PULLING_CALL -> "PULLING_CALL"
    Call.STATE_AUDIO_PROCESSING -> "AUDIO_PROCESSING"
    Call.STATE_SIMULATED_RINGING -> "SIMULATED_RINGING"
    else -> "UNKNOWN: $this"
  }
}
