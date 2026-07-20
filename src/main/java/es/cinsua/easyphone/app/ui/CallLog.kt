package es.cinsua.easyphone.app.ui

import android.app.Application
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.CallLog.Calls
import androidx.lifecycle.AndroidViewModel
import es.cinsua.easyphone.app.ui.telecom.log.CallLogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

enum class CallType(val androidTypes: List<Int>) {
  INCOMING_ANSWERED(Calls.INCOMING_TYPE, Calls.ANSWERED_EXTERNALLY_TYPE, Calls.VOICEMAIL_TYPE),
  INCOMING_MISSED(Calls.MISSED_TYPE, Calls.REJECTED_TYPE, Calls.BLOCKED_TYPE),
  OUTGOING(Calls.OUTGOING_TYPE);

  constructor(vararg telecomTypes: Int) : this(telecomTypes.toList())

  companion object {
    fun fromCallLogType(type: Int) = entries.first { it.androidTypes.contains(type) }
  }
}

fun Context.observeCallLog(): Flow<Unit> = callbackFlow {
  val observer = object : ContentObserver(null) {
    override fun onChange(selfChange: Boolean, uri: Uri?) {
      trySend(Unit)
    }
  }

  contentResolver.registerContentObserver(
      Calls.CONTENT_URI,
      true,
      observer
  )
  trySend(Unit)
  awaitClose {
    contentResolver.unregisterContentObserver(observer)
  }
}

suspend fun AndroidViewModel.fetchRecentCalls(cutOff: Instant) = withContext(Dispatchers.IO) {
  val contentResolver = getApplication<Application>().contentResolver

  val projection = arrayOf(
      Calls.NUMBER,
      Calls.CACHED_NAME,
      Calls.TYPE,
      Calls.DURATION,
      Calls.DATE
  )

  val selection = "${Calls.DATE} >= ?"
  val selectionArgs = arrayOf(cutOff.toEpochMilli().toString())

  val sortOrder = "${Calls.DATE} DESC"

  buildList {
    contentResolver.query(
        Calls.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->
      val numberIdx = cursor.getColumnIndex(Calls.NUMBER)
      val nameIdx = cursor.getColumnIndex(Calls.CACHED_NAME)
      val typeIdx = cursor.getColumnIndex(Calls.TYPE)
      val durationIdx = cursor.getColumnIndex(Calls.DURATION)
      val dateIdx = cursor.getColumnIndex(Calls.DATE)

      // Limit to top 100 entries for better performance
      var count = 0
      while (cursor.moveToNext() && count < 100) {
        val entry = CallLogEntry(
            number = cursor.getString(numberIdx) ?: "Unknown",
            name = cursor.getString(nameIdx)?.takeIf { it.isNotBlank() },
            type = CallType.fromCallLogType(cursor.getInt(typeIdx)),
            duration = Duration.ofSeconds(cursor.getLong(durationIdx)),
            timestamp = cursor.getLong(dateIdx).toLocalDateTime()
        )
        add(entry)
        count++
      }
    }
  }
}

private fun Long.toLocalDateTime() = LocalDateTime.ofInstant(
    Instant.ofEpochMilli(this),
    ZoneId.systemDefault()
)
