package com.example.smsautomationapp.model

data class Campaign(
    val id: Int,
    val title: String,
    val sent: Int = 0,
    val delivered: Int = 0,
    val failed: Int = 0,
    val totalRecipients: Int = 0
) {
    val progress: Int
        get() = if (totalRecipients == 0) 0 else ((sent * 100) / totalRecipients)
}

