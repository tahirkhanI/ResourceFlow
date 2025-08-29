package com.example.resourceflow.ui.admin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.resourceflow.R
import com.example.resourceflow.api.AnnouncementApiService
import com.example.resourceflow.api.AnnouncementRequest
import com.example.resourceflow.api.AnnouncementResponse
import com.example.resourceflow.network.AppConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementsActivity : AppCompatActivity() {

    enum class Recipient(val value: String) {
        STUDENTS("students"),
        FACULTY("faculty"),
        BOTH("both")
    }

    private var currentRecipient = Recipient.STUDENTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcements)

        val btnStudents = findViewById<Button>(R.id.btnStudents)
        val btnFaculty = findViewById<Button>(R.id.btnFaculty)
        val btnBoth = findViewById<Button>(R.id.btnBoth)
        val editSubject = findViewById<EditText>(R.id.editSubject)
        val editMessage = findViewById<EditText>(R.id.editMessage)
        val switchSendNow = findViewById<Switch>(R.id.switchSendNow)
        val btnSendNotification = findViewById<Button>(R.id.btnSendNotification)

        // Initialize recipient button states
        updateRecipientButtons(btnStudents, btnFaculty, btnBoth)

        btnStudents.setOnClickListener {
            currentRecipient = Recipient.STUDENTS
            updateRecipientButtons(btnStudents, btnFaculty, btnBoth)
        }
        btnFaculty.setOnClickListener {
            currentRecipient = Recipient.FACULTY
            updateRecipientButtons(btnStudents, btnFaculty, btnBoth)
        }
        btnBoth.setOnClickListener {
            currentRecipient = Recipient.BOTH
            updateRecipientButtons(btnStudents, btnFaculty, btnBoth)
        }

        btnSendNotification.setOnClickListener {
            val subject = editSubject.text.toString().trim()
            val message = editMessage.text.toString().trim()
            val sendNow = switchSendNow.isChecked

            if (subject.isEmpty()) {
                editSubject.error = "Enter subject"
                editSubject.requestFocus()
                return@setOnClickListener
            }
            if (message.isEmpty()) {
                editMessage.error = "Enter message"
                editMessage.requestFocus()
                return@setOnClickListener
            }

            // If sendNow is false, schedule 5 minutes later (example, you can implement your own picker)
            val sendTimeString = if (sendNow) null else {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MINUTE, 5)
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.time)
            }

            // Get admin ID from saved session or preferences (replace with your actual method)
            val adminId = getAdminIdFromSession()

            val request = AnnouncementRequest(
                title = subject,
                message = message,
                recipient = currentRecipient.value,
                send_now = sendNow,
                send_time = sendTimeString,
                admin_id = adminId
            )

            sendAnnouncement(request, btnSendNotification)
        }
    }

    private fun updateRecipientButtons(btnStudents: Button, btnFaculty: Button, btnBoth: Button) {
        fun active(btn: Button) {
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.accent))
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        }
        fun inactive(btn: Button) {
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light))
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }
        when (currentRecipient) {
            Recipient.STUDENTS -> {
                active(btnStudents)
                inactive(btnFaculty)
                inactive(btnBoth)
            }
            Recipient.FACULTY -> {
                inactive(btnStudents)
                active(btnFaculty)
                inactive(btnBoth)
            }
            Recipient.BOTH -> {
                inactive(btnStudents)
                inactive(btnFaculty)
                active(btnBoth)
            }
        }
    }

    private fun getAdminIdFromSession(): Int? {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return if (prefs.contains("admin_id")) {
            val id = prefs.getInt("admin_id", -1)
            if (id != -1) id else null
        } else {
            null
        }
    }

    private fun sendAnnouncement(request: AnnouncementRequest, btnSend: Button) {
        btnSend.isEnabled = false

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL) // Use your backend URL here
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(AnnouncementApiService::class.java)

        apiService.sendAnnouncement(request).enqueue(object : Callback<AnnouncementResponse> {
            override fun onResponse(call: Call<AnnouncementResponse>, response: Response<AnnouncementResponse>) {
                btnSend.isEnabled = true
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success == true) {
                        Toast.makeText(this@AnnouncementsActivity, "Notification sent successfully", Toast.LENGTH_LONG).show()
                        // Optionally clear fields
                        // findViewById<EditText>(R.id.editSubject).setText("")
                        // findViewById<EditText>(R.id.editMessage).setText("")
                        // findViewById<Switch>(R.id.switchSendNow).isChecked = true
                    } else {
                        Toast.makeText(this@AnnouncementsActivity, "Failed to send: ${body?.error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@AnnouncementsActivity, "Server error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AnnouncementResponse>, t: Throwable) {
                btnSend.isEnabled = true
                Toast.makeText(this@AnnouncementsActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
