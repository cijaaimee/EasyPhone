package es.cinsua.easyphone.app.telecom

import android.content.Intent
import android.os.IBinder
import android.telecom.Call
import android.telecom.InCallService

class InCallService : InCallService() {

  override fun onBind(intent: Intent): IBinder? {
    InCallManager.onServiceBind(this)
    return super.onBind(intent)
  }

  override fun onUnbind(intent: Intent): Boolean {
    InCallManager.onServiceUnbind(this)
    return super.onUnbind(intent)
  }

  override fun onCallAdded(call: Call) {
    InCallManager.onCallAdded(this, call)
  }

  override fun onCallRemoved(call: Call) {
    InCallManager.onCallRemoved(call)
  }
}
