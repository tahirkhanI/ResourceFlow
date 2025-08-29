package com.example.resourceflow.network

data class ResetWithOtpRequest(
    val email: String,
    val otp: String,
    val newpassword: String  // This field name matches PHP input key
)