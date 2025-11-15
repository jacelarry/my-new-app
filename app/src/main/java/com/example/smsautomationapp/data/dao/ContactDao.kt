package com.example.smsautomationapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smsautomationapp.data.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Upsert
    suspend fun upsertAll(items: List<ContactEntity>)

    @Query("DELETE FROM contacts WHERE source = :source")
    suspend fun deleteBySource(source: String)

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun observeAll(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE source = :source ORDER BY name ASC")
    fun observeBySource(source: String): Flow<List<ContactEntity>>
}

