package com.example.resourceflow.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class AnnouncementRequest(
    val title: String,
    val message: String,
    val recipient: String,
    val send_now: Boolean,
    val send_time: String? = null,
    val admin_id: Int? = null
)

data class AnnouncementResponse(
    val success: Boolean? = null,
    val id: Int? = null,
    val error: String? = null
)

interface AnnouncementApiService {
    @Headers("Content-Type: application/json")
    @POST("add_announcement.php")
    fun sendAnnouncement(@Body announcement: AnnouncementRequest): Call<AnnouncementResponse>
}
