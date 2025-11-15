package com.example.smsautomationapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smsautomationapp.Contact
import com.example.smsautomationapp.data.ContactRepository
import com.example.smsautomationapp.data.entity.ContactEntity
import kotlinx.coroutines.launch

class ImportViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ContactRepository(app)

    val csvFlow = repo.observeCsv()
    val phoneFlow = repo.observePhone()
    val allFlow = repo.observeAll()

    fun upsertCsv(contacts: List<Contact>) {
        viewModelScope.launch {
            repo.replaceCsv(contacts.map { ContactEntity(it.phoneNumber, it.name, "CSV") })
        }
    }

    fun upsertPhone(contacts: List<Contact>) {
        viewModelScope.launch {
            repo.addOrReplacePhone(contacts.map { ContactEntity(it.phoneNumber, it.name, "PHONE") })
        }
    }

    fun clearCsv() = viewModelScope.launch { repo.clearCsv() }
    fun clearPhone() = viewModelScope.launch { repo.clearPhone() }
}
