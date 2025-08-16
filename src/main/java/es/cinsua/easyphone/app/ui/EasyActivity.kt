package es.cinsua.easyphone.app.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_VOLUME_DOWN
import android.view.KeyEvent.KEYCODE_VOLUME_UP
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import es.cinsua.easyphone.app.config.DeviceConfig
import org.jetbrains.annotations.MustBeInvokedByOverriders

@SuppressLint("WrongConstant")
abstract class EasyActivity : ComponentActivity() {

  @MustBeInvokedByOverriders
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    actionBar?.hide()
  }

  @MustBeInvokedByOverriders
  override fun onStart() {
    DeviceConfig.onActivityStart(this)
    super.onStart()
  }

  @MustBeInvokedByOverriders
  override fun onStop() {
    super.onStop()
    DeviceConfig.onActivityStop(this)
  }

  @MustBeInvokedByOverriders
  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KEYCODE_VOLUME_DOWN || keyCode == KEYCODE_VOLUME_UP) {
      return true
    }
    return super.onKeyDown(keyCode, event)
  }
}
