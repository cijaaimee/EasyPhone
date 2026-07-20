package es.cinsua.easyphone.app

import android.app.Application
import android.content.Context
import es.cinsua.easyphone.app.lock.AppLockManager

class EasyPhoneApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    AppLockManager.initialize(this)
  }
}

val Application.easyPreferences
  get() = getSharedPreferences("easyphone_preferences", Context.MODE_PRIVATE)
