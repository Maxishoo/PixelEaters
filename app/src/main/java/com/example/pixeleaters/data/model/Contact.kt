package com.example.pixeleaters.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Int,
    val name: String,
    val tags: List<String>
) : Parcelable
