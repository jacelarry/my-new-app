package com.example.smsautomationapp

// Minimal ViewModel container for recipient status used by RecipientStatusAdapter
class DashboardViewModel {
    // State of a single recipient while sending
    enum class State { QUEUED, SENDING, SUCCESS, FAILED, CANCELLED }

    // Data model used by the per-recipient status list
    data class RecipientStatus(
        val number: String,
        val state: State,
        val error: String? = null
    )
}

