package com.example.dz4composekotlin.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val telegram: String?,
    val address: String?,
    val job: String?,
    val tags: List<String>,
    val avatarUrl: String?
) : Parcelable {
    val fullName: String
        get() = "$firstName $lastName"
}
