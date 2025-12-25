package com.example.pixeleaters.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pixeleaters.data.database.AppDatabase
import com.example.pixeleaters.data.model.Contact
import com.example.pixeleaters.data.model.DataState
import com.example.pixeleaters.data.model.PaginationState
import com.example.pixeleaters.data.repository.ContactRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactUiState(
    val contactsState: DataState<List<Contact>> = DataState.Loading(),
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

    private var _allContacts = listOf<Contact>()

    init {
        loadInitialContacts()

        viewModelScope.launch {
            repository.paginationState.collectLatest { paginationState ->
                _uiState.update { it.copy(paginationState = paginationState) }
            }
        }
    }

    fun loadInitialContacts() {
        _uiState.update {
            it.copy(
                contactsState = DataState.Loading(),
                isRefreshing = true
            )
        }

        viewModelScope.launch {
            repository.resetPagination()
            repository.loadInitialContacts().collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        _allContacts = dataState.data
                        applyFilters()
                        _uiState.update {
                            it.copy(
                                contactsState = DataState.Success(_allContacts),
                                isRefreshing = false
                            )
                        }
                    }
                    is DataState.Error -> {
                        _uiState.update {
                            it.copy(
                                contactsState = dataState,
                                isRefreshing = false
                            )
                        }
                    }
                    is DataState.Loading -> {
                        _uiState.update {
                            it.copy(
                                contactsState = dataState,
                                isRefreshing = false
                            )
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
                        val updatedList = _allContacts + result.data
                        _allContacts = updatedList
                        applyFilters()
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
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadInitialContacts()
        }
    }

    fun selectContact(contactId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedContactState = DataState.Loading()) }
            delay(100)

            val result = repository.getContactById(contactId)
            _uiState.update { it.copy(selectedContactState = result) }
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
                    val updatedList = _allContacts + result.data
                    _allContacts = updatedList
                    applyFilters()
                }
                is DataState.Error -> {}
                else -> {}
            }
        }
    }

//    fun updateContact(contact: Contact) {
//        viewModelScope.launch {
//            when (val result = repository.updateContact(contact)) {
//                is DataState.Success -> {
//                    val updatedList = _allContacts.map {
//                        if (it.id == contact.id) result.data else it
//                    }
//                    _allContacts = updatedList
//                    applyFilters()
//
//                    val currentSelected = _uiState.value.selectedContactState
//                    if (currentSelected is DataState.Success && currentSelected.data?.id == contact.id) {
//                        _uiState.update {
//                            it.copy(selectedContactState = DataState.Success(result.data))
//                        }
//                    }
//                }
//                else -> {}
//            }
//        }
//    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            when (val result = repository.deleteContact(contact)) {
                is DataState.Success -> {
                    if (result.data) {
                        val updatedList = _allContacts.filter { it.id != contact.id }
                        _allContacts = updatedList
                        applyFilters()

                        if ((_uiState.value.selectedContactState as? DataState.Success)?.data?.id == contact.id) {
                            clearSelectedContact()
                        }
                    }
                }
                else -> {}
            }
        }
    }

    fun searchContacts(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.length >= 2) {
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
                        applyFilters()
                        _uiState.update { it.copy(isRefreshing = false) }
                    }
                }
            }
        } else {
            applyFilters()
        }
    }

    fun filterByCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }

        if (category != null) {
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
                        applyFilters()
                        _uiState.update { it.copy(isRefreshing = false) }
                    }
                }
            }
        } else {
            applyFilters()
        }
    }

    fun toggleFavorite(contactId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            when (val result = repository.toggleFavorite(contactId, isFavorite)) {
                is DataState.Success -> {
                    val updatedList = _allContacts.map {
                        if (it.id == contactId) result.data else it
                    }
                    _allContacts = updatedList
                    applyFilters()

                    val currentSelected = _uiState.value.selectedContactState
                    if (currentSelected is DataState.Success && currentSelected.data?.id == contactId) {
                        _uiState.update {
                            it.copy(selectedContactState = DataState.Success(result.data))
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val searchQuery = currentState.searchQuery
        val selectedCategory = currentState.selectedCategory

        val filteredList = if (searchQuery.isEmpty() && selectedCategory == null) {
            _allContacts
        } else {
            _allContacts.filter { contact ->
                val matchesSearch = searchQuery.isEmpty() ||
                        contact.firstName.contains(searchQuery, ignoreCase = true) ||
                        contact.lastName.contains(searchQuery, ignoreCase = true) ||
                        contact.phoneNumber?.contains(searchQuery, ignoreCase = true) ?: false ||
                        contact.email?.contains(searchQuery, ignoreCase = true) ?: false

                val matchesCategory = selectedCategory == null ||
                        contact.categories.contains(selectedCategory)

                matchesSearch && matchesCategory
            }
        }

        _uiState.update {
            it.copy(contactsState = DataState.Success(filteredList))
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