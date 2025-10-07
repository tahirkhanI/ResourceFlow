package com.example.resourceflow.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClassroomData(
    val id: String,
    val name: String,
    val floor: Int,
    val resources: List<RoomResource>
) : Parcelable