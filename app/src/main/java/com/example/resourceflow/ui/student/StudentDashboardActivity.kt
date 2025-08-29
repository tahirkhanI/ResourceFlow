package com.example.resourceflow.ui.student

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.resourceflow.R

class StudentDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        // Handle Attendance Card click
        val attendanceCard = findViewById<CardView>(R.id.attendanceCard)
        attendanceCard.setOnClickListener {
            Toast.makeText(this, "Attendance Clicked", Toast.LENGTH_SHORT).show()
        }

        // Handle Grades Card click
        val gradesCard = findViewById<CardView>(R.id.gradesCard)
        gradesCard.setOnClickListener {
            Toast.makeText(this, "Grades Clicked", Toast.LENGTH_SHORT).show()
        }

        // Handle Schedule Card click
        val scheduleCard = findViewById<CardView>(R.id.scheduleCard)
        scheduleCard.setOnClickListener {
            Toast.makeText(this, "Schedule Clicked", Toast.LENGTH_SHORT).show()
        }

        // You may handle Profile image and other logic here as well
    }
}

