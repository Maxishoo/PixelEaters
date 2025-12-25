package com.example.pixeleaters.ui.main


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pixeleaters.data.model.Constants
import com.example.pixeleaters.data.repository.ReminderRepository
import kotlinx.coroutines.launch

/**
 * ViewModel для главного экрана (списка напоминаний), загрузкой данных и состоянием загрузки.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReminderRepository(application.applicationContext)

    private val _reminders = MutableLiveData<List<Constants>>()
    val reminders: LiveData<List<Constants>> = _reminders

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repository.getReminders()
                _reminders.value = data
            } catch (e: Exception) {
                _reminders.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}