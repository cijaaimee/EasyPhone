package es.cinsua.easyphone.app.lock

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppLockManager { // Singleton

    private val _isLocked = MutableStateFlow(true) // Start locked by default or based on persisted state
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

    // --- Optional Persistency ---
    // private const val PREFS_NAME = "AppLockPrefs"
    // private const val KEY_IS_LOCKED = "isLocked"

    // private fun savePersistedLockState(context: Context, locked: Boolean) {
    //     context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    //         .edit()
    //         .putBoolean(KEY_IS_LOCKED, locked)
    //         .apply()
    // }

    // private fun loadPersistedLockState(context: Context): Boolean {
    //     return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    //         .getBoolean(KEY_IS_LOCKED, true) // Default to locked
    // }
}
