package com.example.resourceflow.api

import com.example.resourceflow.model.Complaint
import retrofit2.Call
import retrofit2.http.GET

interface ComplaintApiService {
    @GET("get_complaints.php")
    fun getComplaints(): Call<List<Complaint>>
}
