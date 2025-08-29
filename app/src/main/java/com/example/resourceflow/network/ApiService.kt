package com.example.resourceflow.network

import com.example.resourceflow.ui.admin.BasicResponse
import com.example.resourceflow.ui.admin.ReportsResponse
import com.example.resourceflow.network.ClassroomResponseDto

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("signup.php")
    fun signup(@Body request: SignUpRequest): Call<SignUpResponse>

    @Headers("Content-Type: application/json")
    @POST("login.php")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("send_otp_mail.php")
    fun sendOtpToMail(@Body request: ForgotPasswordOtpRequest): Call<ForgotPasswordOtpResponse>

    @Headers("Content-Type: application/json")
    @POST("verify_otp.php")
    fun resetPasswordWithOtp(@Body request: ResetWithOtpRequest): Call<BasicResponse>

    @GET("reports_admin.php")
    fun getReports(): Call<ReportsResponse>
    @GET("classrooms")
    suspend fun getClassrooms(): Response<ClassroomResponseDto>



    @FormUrlEncoded
    @POST("reports_admin.php")
    fun updateReportStatus(
        @Field("report_id") reportId: Int,
        @Field("status") status: String
    ): Call<BasicResponse>
}
