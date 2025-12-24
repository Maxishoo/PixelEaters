package com.example.pixeleaters.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reminder(
    val id: Int,
    val tag: String,
    val title: String,
    val date: String,
    val time: String,
    val shortDescription: String,
    val description: String = "",
    val context: String = ""
) : Parcelable

