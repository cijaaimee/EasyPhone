package es.cinsua.easyphone.app.contacts

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract

object ContactRepository {

  fun getByPhoneNumber(context: Context, phoneNumber: String): ContactInfo? {
    val contentResolver = context.contentResolver

    val lookupUri =
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon().appendPath(phoneNumber).build()
    val projection =
        arrayOf(
            ContactsContract.PhoneLookup.LOOKUP_KEY,
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup.NUMBER)

    val cursor: Cursor? =
        contentResolver.query(
            lookupUri,
            projection,
            null,
            null,
            null,
            null,
        )

    return cursor?.use {
      if (it.moveToFirst()) {
        val lookupKeyIndex = it.getColumnIndex(ContactsContract.PhoneLookup.LOOKUP_KEY)
        val displayNameIndex = it.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
        val numberIndex = it.getColumnIndex(ContactsContract.PhoneLookup.NUMBER)

        val lookupKey = if (lookupKeyIndex != -1) it.getString(lookupKeyIndex) else null
        val displayName = if (displayNameIndex != -1) it.getString(displayNameIndex) else null
        val phoneNumber = if (numberIndex != -1) it.getString(numberIndex) else null

        if (lookupKey != null && displayName != null && phoneNumber != null) {
          return@use ContactInfo(
              lookupKey = lookupKey, displayName = displayName, phoneNumber = phoneNumber)
        }
      }
      null
    }
  }

  fun getAll(context: Context): List<ContactInfo> {
    val contactsList = mutableListOf<ContactInfo>()
    val contentResolver = context.contentResolver

    val projection =
        arrayOf(
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.STARRED)
    val sortOrder =
        """
        ${ContactsContract.CommonDataKinds.Phone.STARRED} DESC, 
        ${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC
    """
            .trimIndent()
    val cursor =
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, sortOrder)

    cursor?.use {
      val lookupKeyIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)
      val displayNameIndex =
          it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
      val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

      val lookupKeysProcessed = mutableSetOf<String>()

      while (it.moveToNext()) {
        val lookupKey = if (lookupKeyIndex != -1) it.getString(lookupKeyIndex) else continue
        val displayName = if (displayNameIndex != -1) it.getString(displayNameIndex) else continue
        val phoneNumber = if (numberIndex != -1) it.getString(numberIndex) else continue

        if (lookupKeysProcessed.add(lookupKey)) {
          contactsList.add(ContactInfo(lookupKey, displayName, phoneNumber))
        }
      }
    }

    return contactsList
  }
}
