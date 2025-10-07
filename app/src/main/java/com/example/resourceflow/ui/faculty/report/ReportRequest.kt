package com.example.resourceflow.ui.faculty.report

data class ReportRequest(
    val reporter_id: Int,
    val classroom_number: String,
    val problem_description: String
)