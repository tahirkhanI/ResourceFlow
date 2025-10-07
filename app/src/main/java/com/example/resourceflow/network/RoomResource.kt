package com.example.resourceflow.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomResource(
    val name: String,
    val quantity: Int,
    val availability: Boolean
) : Parcelable