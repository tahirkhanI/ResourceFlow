package com.example.resourceflow.network

data class PasswordResetModels(
    val email: String,
    val otp: String,
    val newPassword: String
)

