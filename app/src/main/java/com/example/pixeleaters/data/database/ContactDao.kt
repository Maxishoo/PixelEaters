package com.example.pixeleaters.data.database

import androidx.room.*
import com.example.pixeleaters.data.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact): Long

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY firstName, lastName")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :contactId")
    fun getContactById(contactId: Long): Flow<Contact?>

    @Query("DELETE FROM contacts")
    suspend fun clearAll()
}