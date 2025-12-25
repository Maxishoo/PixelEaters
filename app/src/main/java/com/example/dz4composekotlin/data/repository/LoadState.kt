package com.example.dz4composekotlin.data.repository

sealed class LoadState<out T> {
    object Loading : LoadState<Nothing>()
    data class Success<T>(val data: T) : LoadState<T>()
    data class Error(val throwable: Throwable) : LoadState<Nothing>()
    object Empty : LoadState<Nothing>()
}