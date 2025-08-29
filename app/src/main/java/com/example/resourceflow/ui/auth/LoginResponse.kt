package com.example.resourceflow.network
// LoginResponse.kt
data class LoginResponse(
    val status: String,
    val message: String,
    val role: String,
    val user: User?
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)
