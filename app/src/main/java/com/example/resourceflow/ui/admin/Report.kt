package com.example.resourceflow.ui.admin

import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("report_id") val reportId: Int,
    @SerializedName("reporter_name") val reporterName: String,
    @SerializedName("classroom_number") val classroomNumber: String,
    @SerializedName("problem_description") val problemDescription: String,
    @SerializedName("status") val status: String,
    @SerializedName("reported_at") val reportedAt: String
)
