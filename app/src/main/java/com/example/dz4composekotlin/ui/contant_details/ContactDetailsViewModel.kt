package com.example.dz4composekotlin.ui.contant_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dz4composekotlin.data.model.Contact
import com.example.dz4composekotlin.data.model.Note
import com.example.dz4composekotlin.data.repository.ContactsRepository
import com.example.dz4composekotlin.data.repository.LoadState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactDetailsViewModel(
    private val repository: ContactsRepository
) : ViewModel() {

    private val _contactState = MutableStateFlow<LoadState<Contact>>(LoadState.Loading)
    val contactState: StateFlow<LoadState<Contact>> = _contactState

    private val _notesState = MutableStateFlow<LoadState<List<Note>>>(LoadState.Loading)
    val notesState: StateFlow<LoadState<List<Note>>> = _notesState

    private var allNotes: List<Note> = emptyList()
    private var loadedNotes: List<Note> = emptyList()
    private var currentPage = 0
    private val pageSize = 7

    fun loadContact(contactId: Long) {
        viewModelScope.launch {
            _contactState.value = LoadState.Loading
            _contactState.value = repository.getContactById(contactId)
        }
    }

    fun loadNotes(contactId: Long) {
        viewModelScope.launch {
            _notesState.value = LoadState.Loading

            val result = repository.getNotesForContact(contactId)
            if (result is LoadState.Success) {
                allNotes = result.data
                loadedNotes = allNotes.take(pageSize)
                _notesState.value = if (loadedNotes.isEmpty()) {
                    LoadState.Empty
                } else {
                    LoadState.Success(loadedNotes)
                }
                currentPage = 1
            } else if (result is LoadState.Empty) {
                _notesState.value = LoadState.Empty
            } else {
                _notesState.value = LoadState.Error(Exception("Ошибка загрузки заметок"))
            }
        }
    }

    fun loadNextNotesPage(contactId: Long) {
        viewModelScope.launch {
            if (!canLoadMoreNotes()) return@launch

            delay(1500)

            val startIndex = currentPage * pageSize
            val endIndex = minOf(startIndex + pageSize, allNotes.size)

            val nextPageNotes = allNotes.subList(startIndex, endIndex)
            loadedNotes = loadedNotes + nextPageNotes

            _notesState.value = LoadState.Success(loadedNotes)
            currentPage++
        }
    }

    fun canLoadMoreNotes(): Boolean {
        return loadedNotes.size < allNotes.size
    }
}

class ContactDetailsViewModelFactory(
    private val repository: ContactsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
