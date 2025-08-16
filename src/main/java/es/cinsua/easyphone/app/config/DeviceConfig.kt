package es.cinsua.easyphone.app.config

import android.Manifest.permission.STATUS_BAR
import android.annotation.SuppressLint
import android.app.StatusBarManager
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.STATUS_BAR_SERVICE
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.AudioManager.STREAM_ALARM
import android.media.AudioManager.STREAM_MUSIC
import android.media.AudioManager.STREAM_NOTIFICATION
import android.media.AudioManager.STREAM_RING
import android.media.AudioManager.STREAM_SYSTEM
import android.media.AudioManager.STREAM_VOICE_CALL
import android.util.Log
import es.cinsua.easyphone.app.statusbar.DISABLE2_NONE
import es.cinsua.easyphone.app.statusbar.DISABLE_BACK
import es.cinsua.easyphone.app.statusbar.DISABLE_CLOCK
import es.cinsua.easyphone.app.statusbar.DISABLE_EXPAND
import es.cinsua.easyphone.app.statusbar.DISABLE_HOME
import es.cinsua.easyphone.app.statusbar.DISABLE_NONE
import es.cinsua.easyphone.app.statusbar.DISABLE_NOTIFICATION_ALERTS
import es.cinsua.easyphone.app.statusbar.DISABLE_NOTIFICATION_ICONS
import es.cinsua.easyphone.app.statusbar.DISABLE_ONGOING_CALL_CHIP
import es.cinsua.easyphone.app.statusbar.DISABLE_RECENT
import es.cinsua.easyphone.app.statusbar.DISABLE_SEARCH
import es.cinsua.easyphone.app.statusbar.DISABLE_SYSTEM_INFO
import es.cinsua.easyphone.app.statusbar.disable
import es.cinsua.easyphone.app.statusbar.disable2

object DeviceConfig {

  private val STATUSBAR_DISABLE_FLAGS
    get() =
        DISABLE_NOTIFICATION_ALERTS or
            DISABLE_HOME or
            DISABLE_EXPAND or
            DISABLE_BACK or
            DISABLE_RECENT or
            DISABLE_CLOCK or
            DISABLE_SEARCH or
            DISABLE_NOTIFICATION_ICONS or
            DISABLE_SYSTEM_INFO or
            DISABLE_ONGOING_CALL_CHIP

  private val STATUSBAR_DISABLE2_FLAGS
    get() = DISABLE2_NONE

  private val AUDIO_STREAMS =
      arrayOf(
          STREAM_VOICE_CALL,
          STREAM_SYSTEM,
          STREAM_RING,
          STREAM_MUSIC,
          STREAM_ALARM,
          STREAM_NOTIFICATION)

  fun onActivityStart(context: Context) {
    configureDevice(context)
    disableStatusBar(context)
  }

  fun onActivityStop(context: Context) {
    enableStatusBar(context)
  }

  private fun configureDevice(context: Context) {
    val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
    AUDIO_STREAMS.forEach { stream ->
      val maxVolume = audioManager.getStreamMaxVolume(stream)
      audioManager.setStreamVolume(stream, maxVolume, 0)
    }
  }

  private fun disableStatusBar(context: Context) {
    if (context.checkSelfPermission(STATUS_BAR) == PackageManager.PERMISSION_GRANTED) {
      val statusBarManager = statusBarManager(context)
      statusBarManager.disable(STATUSBAR_DISABLE_FLAGS)
      statusBarManager.disable2(STATUSBAR_DISABLE2_FLAGS)
    } else {
      Log.w("DeviceConfig", "No permission to disable status bar")
    }
  }

  private fun enableStatusBar(context: Context) {
    if (context.checkSelfPermission(STATUS_BAR) == PackageManager.PERMISSION_GRANTED) {
      val statusBarManager = statusBarManager(context)
      statusBarManager.disable(DISABLE_NONE)
      statusBarManager.disable2(DISABLE2_NONE)
    }
  }

  @SuppressLint("WrongConstant")
  private fun statusBarManager(context: Context) =
      context.getSystemService(STATUS_BAR_SERVICE) as StatusBarManager
}
