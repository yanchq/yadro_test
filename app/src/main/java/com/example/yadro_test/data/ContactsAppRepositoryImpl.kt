package com.example.yadro_test.data

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.BitmapFactory
import android.provider.ContactsContract
import com.example.yadro_test.domain.Contact
import com.example.yadro_test.domain.ContactsAppRepository
import com.example.yadro_test.domain.DeleteDuplicatesResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsAppRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val connector: ContactDuplicateServiceConnector
) : ContactsAppRepository {

    override suspend fun deleteDuplicates(): DeleteDuplicatesResult {
        val result = connector.service?.deleteDuplicateContacts()
            ?: throw (RuntimeException("Ошибка подключения к сервису"))
        return when (result) {
            "success" -> {
                DeleteDuplicatesResult.Success
            }
            "nothing" -> DeleteDuplicatesResult.NothingFound
            else -> DeleteDuplicatesResult.Error(result)
        }
    }

    override suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
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

        contacts.distinctBy { it.id }
    }

}