package com.example.dz4composekotlin.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dz4composekotlin.data.model.Contact
import com.example.dz4composekotlin.data.repository.ContactsRepository
import com.example.dz4composekotlin.data.repository.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: ContactsRepository
) : ViewModel() {

    private val _contactsState = MutableStateFlow<LoadState<List<Contact>>>(LoadState.Loading)
    val contactsState: StateFlow<LoadState<List<Contact>>> = _contactsState

    private var currentOffset = 0
    private val pageSize = 10
    private var isLoadingMore = false
    private var hasMore = true

    init {
        loadNextPage()
    }
    fun canLoadMore(): Boolean = hasMore
    fun loadNextPage() {
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true

            val result = repository.getContactsPage(currentOffset, pageSize)

            when (result) {
                is LoadState.Success -> {
                    val currentList = (_contactsState.value as? LoadState.Success<List<Contact>>)?.data ?: emptyList()
                    _contactsState.value = LoadState.Success(currentList + result.data)
                    currentOffset += result.data.size
                    hasMore = repository.hasMoreContacts(currentOffset)
                }
                is LoadState.Empty -> hasMore = false
                is LoadState.Error -> {
                    // Можно оставить старое состояние или показать ошибку
                    _contactsState.value = result
                }
                is LoadState.Loading -> {}
            }

            isLoadingMore = false
        }
    }
}

class ContactsViewModelFactory(private val repository: ContactsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



