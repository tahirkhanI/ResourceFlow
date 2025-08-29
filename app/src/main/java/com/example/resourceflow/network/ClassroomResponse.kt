package com.example.resourceflow.network

data class ClassroomResponse(
    val classrooms: List<Classroom>
)

data class Classroom(
    val id: Int,
    val name: String,
    val floor: Int,
    val resources: List<Resource>
)

data class Resource(
    val name: String,
    val quantity: Int,
    val availability: Boolean
)
