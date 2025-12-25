package com.example.dz4composekotlin.data.repository

import com.example.dz4composekotlin.data.model.Contact
import com.example.dz4composekotlin.data.model.Note
import com.example.dz4composekotlin.data.source.ContactsDataSource

class ContactsRepository(
    private val dataSource: ContactsDataSource
) {

    suspend fun getContactsPage(offset: Int, limit: Int): LoadState<List<Contact>> {
        return try {
            val contacts = dataSource.loadContactsPage(offset, limit)
            if (contacts.isEmpty()) {
                LoadState.Empty
            } else {
                LoadState.Success(contacts)
            }
        } catch (e: Exception) {
            LoadState.Error(e)
        }
    }

    fun hasMoreContacts(offset: Int): Boolean {
        return dataSource.hasMoreContacts(offset)
    }

    suspend fun getContacts(): LoadState<List<Contact>> {
        return try {
            val contacts = dataSource.loadContactsPage(0, Int.MAX_VALUE)
            if (contacts.isEmpty()) {
                LoadState.Empty
            } else {
                LoadState.Success(contacts)
            }
        } catch (e: Exception) {
            LoadState.Error(e)
        }
    }

    suspend fun getContactById(id: Long): LoadState<Contact> {
        return try {
            val contacts = dataSource.loadContactsPage(0, Int.MAX_VALUE)
            val contact = contacts.find { it.id == id }
            contact?.let {
                LoadState.Success(it)
            } ?: LoadState.Empty
        } catch (e: Exception) {
            LoadState.Error(e)
        }
    }

    suspend fun getNotesForContact(contactId: Long): LoadState<List<Note>> {
        return try {
            val notes = dataSource.loadNotes()
                .filter { it.contactId == contactId }

            if (notes.isEmpty()) LoadState.Empty else LoadState.Success(notes)
        } catch (e: Exception) {
            LoadState.Error(e)
        }
    }
}
