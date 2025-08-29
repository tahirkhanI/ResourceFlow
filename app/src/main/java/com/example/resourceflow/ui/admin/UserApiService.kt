package com.example.resourceflow.api

import com.example.resourceflow.model.User
import retrofit2.Call
import retrofit2.http.GET

interface UserApiService {
    @GET("get_users.php")
    fun getUsers(): Call<List<User>>
}
