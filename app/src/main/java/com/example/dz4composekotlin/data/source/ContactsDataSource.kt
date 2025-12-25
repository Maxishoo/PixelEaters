package com.example.dz4composekotlin.data.source

import android.content.Context
import com.example.dz4composekotlin.data.model.Contact
import com.example.dz4composekotlin.data.model.Note
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ContactsDataSource(
    private val context: Context
) {

    private val allContacts: List<Contact> by lazy {
        val json = context.assets
            .open("contacts.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Contact>>() {}.type
        Gson().fromJson(json, type)
    }

    suspend fun loadContactsPage(
        offset: Int,
        limit: Int
    ): List<Contact> = withContext(Dispatchers.IO) {
        delay(2000)

        allContacts
            .drop(offset)
            .take(limit)
    }

    fun hasMoreContacts(offset: Int): Boolean {
        return offset < allContacts.size
    }

    suspend fun loadNotes(): List<Note> {
        delay(2000)

        val json = context.assets
            .open("notes.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Note>>() {}.type
        return Gson().fromJson(json, type)
    }
}
