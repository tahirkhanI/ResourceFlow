package com.example.resourceflow.network

data class BookingRequest(
    val classroom_id: Int,
    val booking_date: String,
    val start_time: String,
    val end_time: String,
    val user_id: Int
)
