package es.cinsua.easyphone.app.ui.home

import androidx.lifecycle.ViewModel
import es.cinsua.easyphone.app.lock.AppLockManager

class HomeViewModel() : ViewModel() {

  val locked = AppLockManager.isLocked

  fun unlock() {
    AppLockManager.unlockApp()
  }
}
