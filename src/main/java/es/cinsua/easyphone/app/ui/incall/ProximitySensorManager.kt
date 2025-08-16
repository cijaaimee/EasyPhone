package es.cinsua.easyphone.app.ui.incall

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProximitySensorManager(context: Context) {

  private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  private val proximitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

  private val _isNear = MutableStateFlow<Boolean?>(null)
  val isNear: StateFlow<Boolean?> = _isNear.asStateFlow()

  private var sensorEventListener: SensorEventListener? = null

  fun startListening() {
    if (proximitySensor == null) {
      Log.w("ProximitySensorManager", "No proximity sensor available.")
      _isNear.value = null
      return
    }

    if (sensorEventListener != null) return

    sensorEventListener =
        object : SensorEventListener {
          override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
              val distance = event.values[0]
              // Determine "near" based on sensor's max range or a common threshold
              val currentlyNear =
                  distance < (proximitySensor.maximumRange.takeIf { it > 0 } ?: 5.0f) &&
                      distance >= 0
              if (_isNear.value != currentlyNear) {
                _isNear.value = currentlyNear
                Log.d(
                    "ProximitySensorManager",
                    "Proximity: Near = $currentlyNear (Distance: $distance)")
              }
            }
          }

          override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

    sensorManager.registerListener(
        sensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
    Log.d("ProximitySensorManager", "Started listening to proximity sensor")
  }

  fun stopListening() {
    if (sensorEventListener != null) {
      sensorManager.unregisterListener(sensorEventListener, proximitySensor)
      sensorEventListener = null
      _isNear.value = null // Reset when not listening
      Log.d("ProximitySensorManager", "Stopped listening to proximity sensor")
    }
  }

  fun isSensorAvailable(): Boolean = proximitySensor != null
}
