package es.cinsua.easyphone.app.contacts

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log

object ContactRepository {

  private val PROJECTION: Array<out String> =
      arrayOf(ContactsContract.Data.DISPLAY_NAME_PRIMARY, ContactsContract.Data.LOOKUP_KEY)

  @SuppressLint("Range")
  fun getByPhoneNumber(context: Context, phoneNumber: String): ContactInfo? {
    var contactInfo: ContactInfo? = null
    val contentResolver = context.contentResolver

    val lookupUri =
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon().appendPath(phoneNumber).build()
    val cursor: Cursor? =
        contentResolver.query(
            lookupUri,
            PROJECTION,
            null,
            null,
            null,
            null,
        )

    cursor?.use { // Ensure the cursor is closed automatically
      if (it.moveToFirst()) {
        // 5. Extract data from the cursor
        val lookupKey = it.getString(it.getColumnIndex(ContactsContract.Data.LOOKUP_KEY))
        val displayName =
            it.getString(it.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY))

        contactInfo = ContactInfo(lookupKey = lookupKey, displayName = displayName)
        Log.d("ContactRepository", "Contact found: $displayName for number $phoneNumber")
      } else {
        Log.d("ContactRepository", "No contact found for number: $phoneNumber")
      }
    }
    return contactInfo
  }
}
