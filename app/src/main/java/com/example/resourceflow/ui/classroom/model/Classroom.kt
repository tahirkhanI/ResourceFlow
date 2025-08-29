package com.example.resourceflow.ui.classroom.model

data class Classroom(
    val id: String,
    val name: String,
    val capacity: Int,
    val location: String,
    val building: String,
    val floor: String,
    val status: RoomStatus,
    val resources: ClassroomResources,
    val features: List<ClassroomFeature>
)
