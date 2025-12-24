package com.example.pixeleaters.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixeleaters.data.model.Reminder
import com.example.pixeleaters.data.model.UiState
import com.example.pixeleaters.data.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel : ViewModel() {

    private val repository = ContactRepository()

    private val _remindersState = MutableStateFlow<UiState<List<Reminder>>>(UiState.Loading)
    val remindersState: StateFlow<UiState<List<Reminder>>> = _remindersState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _expandedReminderId = MutableStateFlow<Int?>(null)
    val expandedReminderId: StateFlow<Int?> = _expandedReminderId.asStateFlow()

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            _remindersState.value = UiState.Loading
            repository.getReminders()
                .onSuccess { reminders ->
                    if (reminders.isEmpty()) {
                        _remindersState.value = UiState.Empty
                    } else {
                        _remindersState.value = UiState.Success(reminders)
                    }
                }
                .onFailure { exception ->
                    _remindersState.value = UiState.Error(exception.message ?: "Unknown error")
                }
        }
    }

    fun searchReminders(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _remindersState.value = UiState.Loading
            repository.searchReminders(query)
                .onSuccess { reminders ->
                    if (reminders.isEmpty()) {
                        _remindersState.value = UiState.Empty
                    } else {
                        _remindersState.value = UiState.Success(reminders)
                    }
                }
                .onFailure { exception ->
                    _remindersState.value = UiState.Error(exception.message ?: "Unknown error")
                }
        }
    }

    fun toggleExpanded(reminderId: Int) {
        _expandedReminderId.value = if (_expandedReminderId.value == reminderId) {
            null
        } else {
            reminderId
        }
    }

    fun collapseAll() {
        _expandedReminderId.value = null
    }

    fun retry() {
        if (_searchQuery.value.isBlank()) {
            loadReminders()
        } else {
            searchReminders(_searchQuery.value)
        }
    }
}

