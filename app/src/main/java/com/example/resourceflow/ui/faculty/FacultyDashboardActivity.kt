package com.example.resourceflow.ui.faculty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.ui.resource.ResourceRequestActivity
import com.example.resourceflow.ui.classroom.ClassroomBookingActivity
import com.example.resourceflow.ui.faculty.FacultyProfileActivity
import com.example.resourceflow.ui.faculty.PostAnnouncementActivity


class FacultyDashboardActivity : AppCompatActivity() {

    private var userId: Int = -1
    private var userName: String? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_dashboard)

        // Retrieve user data from intent
        userId = intent.getIntExtra("USER_ID", -1)
        userName = intent.getStringExtra("USER_NAME")
        userEmail = intent.getStringExtra("USER_EMAIL")

        // Optional: Display user name on top
        val userText = findViewById<TextView>(R.id.tvWelcomeUser)
        userText?.text = "Welcome, $userName"

        // Cards and buttons
        val cardRoomBooking = findViewById<LinearLayout>(R.id.cardRoomBooking)
        val cardResourceRequests = findViewById<LinearLayout>(R.id.cardResourceRequests)
        val cardAnnouncements = findViewById<LinearLayout>(R.id.cardAnnouncements)
        val cardMySchedule = findViewById<LinearLayout>(R.id.cardMySchedule)
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)

        cardRoomBooking.setOnClickListener {
            startActivity(Intent(this, ClassroomBookingActivity::class.java))
        }

        cardResourceRequests.setOnClickListener {
            startActivity(Intent(this, ResourceRequestActivity::class.java))
        }

        cardAnnouncements.setOnClickListener {
            startActivity(Intent(this, PostAnnouncementActivity::class.java))
        }

        cardMySchedule.setOnClickListener {
            // Can use userId here to fetch schedule
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, FacultyProfileActivity::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        val announcementList = listOf(
            Announcement("Faculty Meeting", "Monthly meeting this Friday", "Department", "2 hours ago"),
            Announcement("Exam Schedule", "Final exam dates released", "Academic", "5 hours ago"),
            Announcement("Research Grant", "New research funding open", "Research", "1 day ago")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.announcementsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AnnouncementsAdapter(announcementList)
    }
}

data class Announcement(
    val title: String,
    val description: String,
    val tag: String,
    val timeAgo: String
)
