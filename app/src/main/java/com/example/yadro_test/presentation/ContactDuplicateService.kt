package com.example.yadro_test.presentation

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import com.example.yadro_test.IContactDuplicateService
import com.example.yadro_test.domain.Contact
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDuplicateService : Service() {

    val binder = object : IContactDuplicateService.Stub() {
        override fun deleteDuplicateContacts(): String? {
            var found = "nothing"
            return try {
                val contacts = getActualContacts()
                var prev = contacts[0]

                for (i in 1 until contacts.size) {
                    if (contacts[i].name == prev.name && contacts[i].phoneNumber == prev.phoneNumber
                        && contacts[i].photo == prev.photo
                    ) {
                        val uriToDelete = ContentUris.withAppendedId(
                            ContactsContract.RawContacts.CONTENT_URI,
                            getRawContactId(contacts[i].id.toLong())!!
                        )
                        contentResolver.delete(uriToDelete, null, null)
                        found = "success"
                    } else {
                        prev = contacts[i]
                    }
                }

                found
            } catch (e: Exception) {
                e.message
            }
        }

    }

    private fun getActualContacts(): List<Contact> {
        var contacts = mutableListOf<Contact>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)?.replace("\\s".toRegex(), "") ?: continue

                val contactUri =
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    contentResolver,
                    contactUri
                )
                val photo = inputStream?.use { stream -> BitmapFactory.decodeStream(stream) }

                contacts.add(
                    Contact(
                        id = id.toInt(),
                        name = name,
                        phoneNumber = number,
                        photo = photo
                    )
                )
            }
        }
        for (contact in contacts.distinctBy { it.id }) {
            Log.d("DuplicateCheck", contact.toString())
        }
        return contacts.distinctBy { it.id }
    }

    fun getRawContactId(contactId: Long): Long? {
        val projection = arrayOf(ContactsContract.RawContacts._ID)
        val selection = "${ContactsContract.RawContacts.CONTACT_ID} = ?"
        val selectionArgs = arrayOf(contactId.toString())

        val cursor = contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getLong(it.getColumnIndexOrThrow(ContactsContract.RawContacts._ID))
            }
        }

        return null
    }

    override fun onBind(p0: Intent?): IBinder? = binder
}