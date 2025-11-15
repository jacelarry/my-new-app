package com.example.smsautomationapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val phone: String,
    val name: String,
    val source: String // "CSV" or "PHONE"
)

