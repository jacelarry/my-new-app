package com.example.smsautomationapp.data

import android.content.Context
import com.example.smsautomationapp.data.db.AppDatabase
import com.example.smsautomationapp.data.entity.ContactEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ContactRepository(context: Context) {
    private val dao = AppDatabase.get(context).contactDao()

    fun observeAll(): Flow<List<ContactEntity>> = dao.observeAll()
    fun observeCsv(): Flow<List<ContactEntity>> = dao.observeBySource("CSV")
    fun observePhone(): Flow<List<ContactEntity>> = dao.observeBySource("PHONE")

    suspend fun replaceCsv(list: List<ContactEntity>) = withContext(Dispatchers.IO) {
        dao.deleteBySource("CSV")
        if (list.isNotEmpty()) dao.upsertAll(list)
    }

    suspend fun addOrReplacePhone(list: List<ContactEntity>) = withContext(Dispatchers.IO) {
        dao.deleteBySource("PHONE")
        if (list.isNotEmpty()) dao.upsertAll(list)
    }

    suspend fun clearCsv() = withContext(Dispatchers.IO) { dao.deleteBySource("CSV") }
    suspend fun clearPhone() = withContext(Dispatchers.IO) { dao.deleteBySource("PHONE") }
    suspend fun clearAll() = withContext(Dispatchers.IO) { dao.deleteAll() }
}

