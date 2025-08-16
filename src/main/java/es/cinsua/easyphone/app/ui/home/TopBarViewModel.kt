package es.cinsua.easyphone.app.ui.home

import android.app.Application
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.IntentFilter
import android.media.AudioManager
import android.os.BatteryManager
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import es.cinsua.easyphone.app.telecom.TelecomDialer
import es.cinsua.easyphone.app.toast.ToastManager
import es.cinsua.easyphone.app.tts.TtsEngine
import es.cinsua.easyphone.app.ui.home.RingerModeState.Companion.toRingerModeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RingerModeState {
  NORMAL,
  VIBRATE,
  SILENT;

  companion object {
    internal fun Int.toRingerModeState() =
        when (this) {
          AudioManager.RINGER_MODE_NORMAL -> NORMAL
          AudioManager.RINGER_MODE_VIBRATE -> VIBRATE
          else -> SILENT
        }
  }
}

class TopBarViewModel(application: Application) : AndroidViewModel(application) {

  private val audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
  private val notificationManager =
      application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  private val _batteryLevel = MutableStateFlow(0.0F)
  val batteryLevel = _batteryLevel.asStateFlow()

  private val _charging = MutableStateFlow(false)
  val charging = _charging.asStateFlow()

  private val _ringerModeState = MutableStateFlow(audioManager.ringerMode.toRingerModeState())
  val ringerModeState = _ringerModeState.asStateFlow()

  private val batteryReceiver =
      object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
          val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

          if (level != -1 && scale != -1) {
            _batteryLevel.value = level / scale.toFloat()
          }

          val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
          _charging.value =
              status == BatteryManager.BATTERY_STATUS_CHARGING ||
                  status == BatteryManager.BATTERY_STATUS_FULL
        }
      }

  private val ringerModeReceiver =
      object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          if (intent.action == AudioManager.RINGER_MODE_CHANGED_ACTION) {
            val newMode =
                intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL)
            _ringerModeState.value = newMode.toRingerModeState()
          }
        }
      }

  private val tts = TtsEngine(application)

  init {
    application.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    application.registerReceiver(
        ringerModeReceiver, IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION))
  }

  fun onEmergencyClicked() {
    tts.speak("Llamando a Emergencias") {
      TelecomDialer.dial(application.applicationContext, "112")
    }
  }

  fun onBatteryClicked() {
    val batteryPct = (batteryLevel.value * 100).toInt()
    ToastManager.show("Batería: $batteryPct%")
    tts.speak("Batería al $batteryPct%")
  }

  fun onRingerClicked() {
    if (notificationManager.isNotificationPolicyAccessGranted) {
      if (ringerModeState.value == RingerModeState.NORMAL) {
        tts.speak(" Sonido apagado") { audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT }
      } else {
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        tts.speak(" Sonido encendido")
      }
    } else {
      // TODO move out of here into a permissions screen
      val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
      intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
      application.startActivity(intent)
    }
  }

  override fun onCleared() {
    super.onCleared()

    val application = getApplication<Application>()
    application.unregisterReceiver(batteryReceiver)
    application.unregisterReceiver(ringerModeReceiver)
    tts.shutdown()
  }
}
