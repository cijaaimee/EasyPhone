package es.cinsua.easyphone.app.lock

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppLockManager {

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private var screenOffReceiver: BroadcastReceiver? = null

    fun initialize(application: Application) {
        if (screenOffReceiver == null) {

            screenOffReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == Intent.ACTION_SCREEN_OFF) {
                        _isLocked.value = true
                    }
                }
            }
            val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
            application.registerReceiver(screenOffReceiver, filter)
        }
    }

    fun unlockApp() {
        _isLocked.value = false
    }
}
