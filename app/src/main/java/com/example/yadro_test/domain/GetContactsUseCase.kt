package com.example.yadro_test.domain

import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val repository: ContactsAppRepository
) {
    suspend operator fun invoke(): List<Contact> {
        return repository.getContacts()
    }
}