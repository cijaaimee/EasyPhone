package es.cinsua.easyphone.app.telecom

import android.content.Context
import android.content.Context.TELECOM_SERVICE
import android.net.Uri
import android.telecom.TelecomManager
import android.util.Log

object TelecomDialer {
  fun dial(context: Context, number: String) {
    val telecomManager = context.getSystemService(TELECOM_SERVICE) as TelecomManager
    val uri = Uri.fromParts("tel", number, null)

    Log.d("TelecomDialer", "Attempting to place call to $number")

    try {
      telecomManager.placeCall(uri, null)
    } catch (e: SecurityException) {
      Log.e("TelecomDialer", "Permission error placing call", e)
    }
  }
}
