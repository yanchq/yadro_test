package com.example.yadro_test.domain

interface ContactsAppRepository {

    suspend fun deleteDuplicates(): DeleteDuplicatesResult

    suspend fun getContacts(): List<Contact>
}

sealed class DeleteDuplicatesResult() {
    object Success : DeleteDuplicatesResult()
    object NothingFound : DeleteDuplicatesResult()
    data class Error(val message: String) : DeleteDuplicatesResult()
}