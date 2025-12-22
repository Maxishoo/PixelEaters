package com.example.pixeleaters.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pixeleaters.MainActivity
import com.example.pixeleaters.data.database.AppDatabase
import com.example.pixeleaters.data.model.Contact
import com.example.pixeleaters.data.model.DataState
import com.example.pixeleaters.data.model.PaginationState
import com.example.pixeleaters.data.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactUiState(
    val contactsState: DataState<List<Contact>> = DataState.Loading(),
    val allContacts: List<Contact> = emptyList(),
    val selectedContactState: DataState<Contact?> = DataState.Loading(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val showAddDialog: Boolean = false,
    val paginationState: PaginationState = PaginationState(),
    val isRefreshing: Boolean = false
)

class ContactViewModel(
    private val repository: ContactRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()

    private val _contacts = mutableListOf<Contact>()

    init {
        loadInitialContacts()

        viewModelScope.launch {
            repository.paginationState.collect { paginationState ->
                _uiState.update { it.copy(paginationState = paginationState) }
            }
        }
    }

    fun loadInitialContacts() {
        _contacts.clear()
        repository.resetPagination()

        viewModelScope.launch {
            repository.loadInitialContacts().collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        _contacts.addAll(dataState.data)
                        _uiState.update {
                            it.copy(
                                contactsState = DataState.Success(_contacts.toList()),
                                allContacts = _contacts.toList()
                            )
                        }
                    }
                    is DataState.Error -> {
                        _uiState.update {
                            it.copy(contactsState = dataState)
                        }
                    }
                    is DataState.Loading -> {
                        _uiState.update {
                            it.copy(contactsState = dataState)
                        }
                    }
                }
            }
        }
    }

    fun loadMoreContacts() {
        val currentState = _uiState.value

        if (currentState.paginationState.isLoadingMore ||
            !currentState.paginationState.hasMore ||
            currentState.searchQuery.isNotEmpty() ||
            currentState.selectedCategory != null) {
            return
        }

        viewModelScope.launch {
            when (val result = repository.loadNextPage()) {
                is DataState.Success -> {
                    if (result.data.isNotEmpty()) {
                        _contacts.addAll(result.data)
                        _uiState.update {
                            it.copy(
                                contactsState = DataState.Success(_contacts.toList()),
                                allContacts = _contacts.toList()
                            )
                        }
                    }
                }
                is DataState.Error -> {
                    _uiState.update {
                        it.copy(
                            paginationState = it.paginationState.copy(
                                errorLoadingMore = result.message
                            )
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun refreshContacts() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadInitialContacts()
        _uiState.update { it.copy(isRefreshing = false) }
    }

    fun selectContact(contactId: Long) {
        _uiState.update { it.copy(selectedContactState = DataState.Loading()) }

        viewModelScope.launch {
            repository.getContactById(contactId).collect { dataState ->
                _uiState.update { it.copy(selectedContactState = dataState) }
            }
        }
    }

    fun clearSelectedContact() {
        _uiState.update { it.copy(selectedContactState = DataState.Success(null)) }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun addContact(contact: Contact) {
        viewModelScope.launch {
            when (val result = repository.addContact(contact)) {
                is DataState.Success -> {
                    hideAddDialog()
                    loadInitialContacts()
                }
                is DataState.Error -> {
                    // Можно показать snackbar с ошибкой
                }
                else -> {}
            }
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            repository.updateContact(contact)
            loadInitialContacts()
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
            if ((_uiState.value.selectedContactState as? DataState.Success)?.data?.id == contact.id) {
                clearSelectedContact()
            }
            loadInitialContacts()
        }
    }

    fun searchContacts(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isEmpty()) {
            _uiState.update {
                it.copy(contactsState = DataState.Success(_contacts))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            when (val result = repository.searchContacts(query)) {
                is DataState.Success -> {
                    _uiState.update {
                        it.copy(
                            contactsState = result,
                            isRefreshing = false
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    fun filterByCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }

        if (category == null) {
            _uiState.update {
                it.copy(contactsState = DataState.Success(_contacts))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            when (val result = repository.getContactsByCategory(category)) {
                is DataState.Success -> {
                    _uiState.update {
                        it.copy(
                            contactsState = result,
                            isRefreshing = false
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    fun toggleFavorite(contactId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(contactId, isFavorite)
            loadInitialContacts()
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    ?: throw IllegalStateException("Application must be provided")

                val database = AppDatabase.getDatabase(application)
                val contactDao = database.contactDao()
                val repository = ContactRepository(contactDao)

                ContactViewModel(repository)
            }
        }
    }
}