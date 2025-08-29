package com.example.resourceflow.network

import com.google.gson.annotations.SerializedName

data class ClassroomDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("resources") val resources: List<ResourceDto>
)


data class ResourceDto(
    val name: String,
    val quantity: Int
)
