package com.example.pixeleaters.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val telegram: String? = null,
    val address: String? = null,
    val workplace: String? = null,
    val categories: List<String> = emptyList(),
    val profileImageUrl: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class ApiResponse<T>(
    val data: T,
    val success: Boolean = true,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class DataState<T> {
    data class Loading<T>(val message: String = "Загрузка...") : DataState<T>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error<T>(val message: String, val exception: Throwable? = null) : DataState<T>()
}

data class PaginationState(
    val currentPage: Int = 0,
    val pageSize: Int = 20,
    val totalItems: Int = 0,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val errorLoadingMore: String? = null
)