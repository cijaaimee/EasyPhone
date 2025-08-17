package es.cinsua.easyphone.app

import android.app.Application
import es.cinsua.easyphone.app.lock.AppLockManager

class EasyPhoneApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    AppLockManager.initialize(this)
  }
}
