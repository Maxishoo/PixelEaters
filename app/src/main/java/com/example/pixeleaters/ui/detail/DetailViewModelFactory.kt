package com.example.pixeleaters.ui.detail


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val context: Context,
    private val reminderId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(context.applicationContext as android.app.Application, reminderId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}