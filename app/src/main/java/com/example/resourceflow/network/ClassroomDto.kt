package com.example.resourceflow.network

data class ClassroomsResponse(
    val classrooms: List<ClassroomDto>
)

data class ClassroomDto(
    val id: String,
    val name: String,
    val floor: Int,
    val resources: List<ResourceDto>
)

data class ResourceDto(
    val name: String,
    val quantity: Int,
    val availability: Boolean
)