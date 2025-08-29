package com.example.resourceflow.ui.classroom.model

data class ClassroomUi(
    val id: String,
    val name: String,
    val capacity: Int,
    val powerOutlets: Int,
    val tables: Int,
    val chairs: Int,
    val hasProjector: Boolean,
    val hasAc: Boolean,
    val hasFan: Boolean,
    val hasWifi: Boolean
)
