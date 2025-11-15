package com.example.smsautomationapp

object ContactRepository {
    val contacts = mutableListOf<Contact>()

    fun addContacts(newContacts: List<Contact>) {
        contacts.addAll(newContacts)
    }

    fun getAllContacts(): List<Contact> {
        return contacts.distinctBy { it.phoneNumber } // Remove duplicates
    }

    fun clearContacts() {
        contacts.clear()
    }
}