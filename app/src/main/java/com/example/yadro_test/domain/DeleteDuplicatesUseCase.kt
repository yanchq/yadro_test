package com.example.yadro_test.domain

import javax.inject.Inject

class DeleteDuplicatesUseCase @Inject constructor(
    private val repository: ContactsAppRepository
) {
    suspend operator fun invoke(): DeleteDuplicatesResult {
        return repository.deleteDuplicates()
    }
}