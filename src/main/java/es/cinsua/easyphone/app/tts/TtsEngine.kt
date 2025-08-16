package es.cinsua.easyphone.app.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID

class TtsEngine {

  private val tts: TextToSpeech
  private var ttsAvailable = false

  private var currentUtteranceId: String? = null
  private var currentOnComplete: (() -> Unit)? = null

  constructor(context: Context) {
    val locale = Locale("es_ES")
    this.tts =
        TextToSpeech(context) { status ->
          if (status == TextToSpeech.SUCCESS) {
            ttsAvailable = true
          }
        }
    tts.language = locale
    tts.setOnUtteranceProgressListener(
        object : UtteranceProgressListener() {
          override fun onDone(utteranceId: String?) {
            utteranceId?.let(::onUtteranceComplete)
          }

          override fun onError(utteranceId: String?) {
            utteranceId?.let(::onUtteranceComplete)
          }

          override fun onStart(utteranceId: String?) {}
        })
  }

  private fun onUtteranceComplete(utteranceId: String) {
    if (utteranceId == currentUtteranceId) {
      currentOnComplete?.invoke()

      currentUtteranceId = null
      currentOnComplete = null
    }
  }

  fun speak(text: String, onComplete: (() -> Unit)? = null) {
    if (ttsAvailable) {
      tts.stop()

      currentUtteranceId = UUID.randomUUID().toString()
      currentOnComplete = onComplete

      tts.speak(text, QUEUE_FLUSH, null, currentUtteranceId)
    } else {
      onComplete?.invoke()
    }
  }

  fun shutdown() {
    tts.shutdown()
  }
}
