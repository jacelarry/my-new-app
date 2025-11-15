package com.example.smsautomationapp.network

data class StkPushRequest(
    val BusinessShortCode: String,
    val Passkey: String,
    val Timestamp: String,
    val TransactionType: String,
    val Amount: String,
    val PartyA: String,
    val PartyB: String,
    val PhoneNumber: String,
    val CallBackURL: String,
    val AccountReference: String,
    val TransactionDesc: String
)
