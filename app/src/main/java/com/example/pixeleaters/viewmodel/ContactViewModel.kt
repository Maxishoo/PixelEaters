package com.example.pixeleaters.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixeleaters.data.model.Contact
import com.example.pixeleaters.data.model.UiState
import com.example.pixeleaters.data.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactViewModel : ViewModel() {

    private val repository = ContactRepository()

    private val _contactsState = MutableStateFlow<UiState<List<Contact>>>(UiState.Loading)
    val contactsState: StateFlow<UiState<List<Contact>>> = _contactsState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            _contactsState.value = UiState.Loading
            repository.getContacts()
                .onSuccess { contacts ->
                    if (contacts.isEmpty()) {
                        _contactsState.value = UiState.Empty
                    } else {
                        _contactsState.value = UiState.Success(contacts)
                    }
                }
                .onFailure { exception ->
                    _contactsState.value = UiState.Error(exception.message ?: "Unknown error")
                }
        }
    }

    fun searchContacts(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _contactsState.value = UiState.Loading
            repository.searchContacts(query)
                .onSuccess { contacts ->
                    if (contacts.isEmpty()) {
                        _contactsState.value = UiState.Empty
                    } else {
                        _contactsState.value = UiState.Success(contacts)
                    }
                }
                .onFailure { exception ->
                    _contactsState.value = UiState.Error(exception.message ?: "Unknown error")
                }
        }
    }

    fun retry() {
        if (_searchQuery.value.isBlank()) {
            loadContacts()
        } else {
            searchContacts(_searchQuery.value)
        }
    }
}
