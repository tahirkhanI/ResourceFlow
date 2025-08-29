package com.example.resourceflow.model

data class Complaint(
    val id: Int,
    val user_id: Int,
    val classroom_id: Int,
    val room_number: String,
    val issue_description: String,
    val status: String,
    val created_at: String
)
