package com.example.pixeleaters.ui.detail


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pixeleaters.R
import com.example.pixeleaters.data.model.Constants
import com.example.pixeleaters.data.repository.ReminderRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    application: Application,
    private val reminderId: String
) : AndroidViewModel(application) {

    private val repository = ReminderRepository(application.applicationContext)

    private val _reminder = MutableLiveData<Constants?>()
    val reminder: LiveData<Constants?> = _reminder

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadReminder()
    }

    private fun loadReminder() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val data = repository.getReminderById(reminderId)
                if (data != null) {
                    _reminder.value = data
                } else {
                    _error.value = getApplication<Application>().getString(R.string.error_reminder_not_found)
                }
            } catch (e: Exception) {
                _error.value = getApplication<Application>().getString(R.string.error_loading_reminder)
            } finally {
                _isLoading.value = false
            }
        }
    }
}