package com.example.smsautomationapp

enum class ContactSource {
    PHONE,
    CSV
}

data class Contact(
    val name: String,
    val phoneNumber: String,
    val source: ContactSource
)
