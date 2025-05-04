package com.example.yadro_test.presentation

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yadro_test.IContactDuplicateService
import com.example.yadro_test.domain.Contact
import com.example.yadro_test.domain.DeleteDuplicatesResult
import com.example.yadro_test.domain.DeleteDuplicatesUseCase
import com.example.yadro_test.domain.GetContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: Application,
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteDuplicatesUseCase: DeleteDuplicatesUseCase
) : AndroidViewModel(app) {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>>
        get() = _contacts

    private val _serviceResult = MutableStateFlow<DeleteDuplicatesResult?>(null)
    val serviceResult: StateFlow<DeleteDuplicatesResult?>
        get() = _serviceResult

    private val _buttonState = MutableStateFlow<Boolean>(true)
    val buttonState: StateFlow<Boolean>
        get() = _buttonState



    fun getContacts() {
        viewModelScope.launch {
            _contacts.value = getContactsUseCase()
        }
    }

    fun deleteDuplicates() {
        _buttonState.value = false
        viewModelScope.launch(Dispatchers.IO) {
            _serviceResult.value = deleteDuplicatesUseCase()
        }
        _buttonState.value = true
    }

}