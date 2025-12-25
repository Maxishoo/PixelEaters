package com.example.pixeleaters.data.repository

import com.example.pixeleaters.data.database.ContactDao
import com.example.pixeleaters.data.mock.MockDataSource
import com.example.pixeleaters.data.model.Contact
import com.example.pixeleaters.data.model.DataState
import com.example.pixeleaters.data.model.PaginationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactDao: ContactDao
) {

    private val mockDataSource = MockDataSource()
    private val _paginationState = MutableStateFlow(PaginationState())
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    fun loadInitialContacts(): Flow<DataState<List<Contact>>> = flow {
        emit(DataState.Loading(""))

        try {
            val apiResponse = mockDataSource.getContactsInitial(_paginationState.value.pageSize)

            if (apiResponse.success) {
                val contacts = apiResponse.data

                contacts.forEach { contactDao.insert(it) }

                _paginationState.update {
                    it.copy(
                        currentPage = 0,
                        totalItems = 100,
                        hasMore = contacts.size >= _paginationState.value.pageSize
                    )
                }

                emit(DataState.Success(contacts))
            } else {
                contactDao.getAllContacts().collect { dbContacts ->
                    if (dbContacts.isNotEmpty()) {
                        emit(DataState.Success(dbContacts))
                    } else {
                        emit(DataState.Error(apiResponse.message ?: ""))
                    }
                }
            }
        } catch (e: Exception) {
            emit(DataState.Error("${e.message}"))
        }
    }

    suspend fun loadNextPage(): DataState<List<Contact>> {
        val currentState = _paginationState.value

        if (!currentState.hasMore || currentState.isLoadingMore) {
            return DataState.Success(emptyList())
        }

        _paginationState.update { it.copy(isLoadingMore = true, errorLoadingMore = null) }

        return try {
            val nextPage = currentState.currentPage + 1
            val apiResponse = mockDataSource.getContactsPaginated(nextPage, currentState.pageSize)

            if (apiResponse.success) {
                val newContacts = apiResponse.data

                newContacts.forEach { contactDao.insert(it) }

                _paginationState.update {
                    it.copy(
                        currentPage = nextPage,
                        hasMore = newContacts.isNotEmpty(),
                        isLoadingMore = false,
                        totalItems = maxOf(it.totalItems, nextPage * currentState.pageSize + newContacts.size)
                    )
                }

                DataState.Success(newContacts)
            } else {
                _paginationState.update {
                    it.copy(
                        isLoadingMore = false,
                        errorLoadingMore = apiResponse.message
                    )
                }
                DataState.Error(apiResponse.message ?: "")
            }
        } catch (e: Exception) {
            _paginationState.update {
                it.copy(
                    isLoadingMore = false,
                    errorLoadingMore = e.message
                )
            }
            DataState.Error("${e.message}")
        }
    }

    fun resetPagination() {
        _paginationState.update {
            it.copy(
                currentPage = 0,
                hasMore = true,
                isLoadingMore = false,
                errorLoadingMore = null
            )
        }
    }

    suspend fun getContactById(contactId: Long): DataState<Contact?> {
        return try {
            val contact = contactDao.getContactByIdSync(contactId)
            if (contact != null) {
                DataState.Success(contact)
            } else {
                DataState.Error("")
            }
        } catch (e: Exception) {
            DataState.Error("${e.message}")
        }
    }

    suspend fun addContact(contact: Contact): DataState<Contact> {
        return try {
            val apiResponse = mockDataSource.addContact(contact)

            if (apiResponse.success) {
                val newContact = apiResponse.data
                contactDao.insert(newContact)
                DataState.Success(newContact)
            } else {
                val localId = contactDao.insert(contact)
                val localContact = contact.copy(id = localId)
                DataState.Success(localContact)
            }
        } catch (e: Exception) {
            DataState.Error("${e.message}")
        }
    }

    suspend fun deleteContact(contact: Contact): DataState<Boolean> {
        return try {
            val apiResponse = mockDataSource.deleteContact(contact.id)

            if (apiResponse.success) {
                contactDao.delete(contact)
                DataState.Success(true)
            } else {
                DataState.Error(apiResponse.message ?: "")
            }
        } catch (e: Exception) {
            DataState.Error("${e.message}")
        }
    }

    suspend fun searchContacts(query: String): DataState<List<Contact>> {
        return try {
            val apiResponse = mockDataSource.searchContacts(query)

            if (apiResponse.success) {
                DataState.Success(apiResponse.data)
            } else {
                val localResults = contactDao.getAllContacts()
                    .firstOrNull()
                    ?.filter { contact ->
                        "${contact.firstName} ${contact.lastName}".contains(query, ignoreCase = true) ||
                                contact.phoneNumber?.contains(query, ignoreCase = true) == true ||
                                contact.email?.contains(query, ignoreCase = true) == true
                    } ?: emptyList()

                if (localResults.isNotEmpty()) {
                    DataState.Success(localResults)
                } else {
                    DataState.Error(apiResponse.message ?: "")
                }
            }
        } catch (e: Exception) {
            DataState.Error("${e.message}")
        }
    }

    suspend fun getContactsByCategory(category: String): DataState<List<Contact>> {
        return try {
            val apiResponse = mockDataSource.getContactsByCategory(category)

            if (apiResponse.success) {
                DataState.Success(apiResponse.data)
            } else {
                val localResults = contactDao.getAllContacts()
                    .firstOrNull()
                    ?.filter { it.categories.contains(category) } ?: emptyList()

                DataState.Success(localResults)
            }
        } catch (e: Exception) {
            DataState.Error("${e.message}")
        }
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean): DataState<Contact> {
        return try {
            val apiResponse = mockDataSource.toggleFavorite(id, isFavorite)

            if (apiResponse.success && apiResponse.data != null) {
                contactDao.update(apiResponse.data)
                DataState.Success(apiResponse.data)
            } else {
                DataState.Error(apiResponse.message ?: "")
            }
        } catch (e: Exception) {
            DataState.Error("${e.message}")
        }
    }
}