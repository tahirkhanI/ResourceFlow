package com.example.resourceflow.network

import com.example.resourceflow.network.BasicResponse
import com.example.resourceflow.ui.admin.ReportsResponse
import com.example.resourceflow.network.ClassroomResponseDto
import com.example.resourceflow.network.LoginRequest
import com.example.resourceflow.network.LoginResponse
import com.example.resourceflow.network.SignUpRequest
import com.example.resourceflow.network.SignUpResponse
import com.example.resourceflow.ui.faculty.report.ReportRequest
import com.example.resourceflow.ui.faculty.report.ReportResponse
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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

    @FormUrlEncoded
    @POST("reports_admin.php")
    fun updateReportStatus(
        @Field("report_id") reportId: Int,
        @Field("status") status: String
    ): Call<BasicResponse>



    @GET("get_Classrooms.php")
    fun getClassrooms(): Call<ClassroomsResponse>


    @Headers("Content-Type: application/json")
    @POST("bookclassroom.php")   // adjust path if your PHP is in a subfolder
    fun bookClassroom(
        @Body bookingRequest: BookingRequest
    ): Call<BasicResponse>



    interface ReportApi {
        @POST("report_fac.php")
        fun submitReport(@Body report: ReportRequest): Call<ReportResponse>
    }

}