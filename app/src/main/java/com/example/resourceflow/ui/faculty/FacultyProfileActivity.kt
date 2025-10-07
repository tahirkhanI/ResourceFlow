package com.example.resourceflow.ui.faculty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R
import com.example.resourceflow.ui.auth.LoginActivity



class FacultyProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_profile)

        // UI References
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvSchool = findViewById<TextView>(R.id.tvSchool)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvOffice = findViewById<TextView>(R.id.tvOffice)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)

        val imageProfile = findViewById<ImageView>(R.id.imageProfile)

        // Placeholder: Fetch and set faculty data here (replace with backend CALL)
        tvName.text = "Suresh"
        tvTitle.text = "Professor of Computer Science"
        tvSchool.text = "School of Engineering"
        tvEmail.text = "fac@test.com"
        tvOffice.text = "Room 202, SCAD"
        tvPhone.text = "793617528"

        // For imageProfile: Use Glide/Picasso for backend images

        btnBack.setOnClickListener { finish() }

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            // Optionally: finish()
        }
    }
}
