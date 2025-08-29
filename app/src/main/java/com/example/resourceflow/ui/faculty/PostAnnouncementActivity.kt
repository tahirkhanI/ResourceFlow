package com.example.resourceflow.ui.faculty

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R

class PostAnnouncementActivity : AppCompatActivity() {

    private lateinit var inputTitle: EditText
    private lateinit var inputMessage: EditText
    private lateinit var rbAllStudents: RadioButton
    private lateinit var rbEntireClass: RadioButton
    private lateinit var btnSend: TextView
    private lateinit var btnCancel: TextView
    private lateinit var btnPreview: Button
    private lateinit var attachPanel: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_announcement)

        inputTitle = findViewById(R.id.inputTitle)
        inputMessage = findViewById(R.id.inputMessage)
        rbAllStudents = findViewById(R.id.rbAllStudents)
        rbEntireClass = findViewById(R.id.rbEntireClass)
        btnSend = findViewById(R.id.btnSend)
        btnCancel = findViewById(R.id.btnCancel)
        btnPreview = findViewById(R.id.btnPreview)
        attachPanel = findViewById(R.id.attachPanel)

        // Default selection
        rbAllStudents.isChecked = true

        btnCancel.setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {
            sendAnnouncement()
        }

        btnPreview.setOnClickListener {
            previewAnnouncement()
        }

        attachPanel.setOnClickListener {
            // TODO: Integrate file picker here
            Toast.makeText(this, "Attachment feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendAnnouncement() {
        val title = inputTitle.text.toString().trim()
        val message = inputMessage.text.toString().trim()
        val sendTo = if (rbAllStudents.isChecked) "All Students" else "Entire Class"

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Title and message cannot be empty.", Toast.LENGTH_SHORT).show()
        } else {
            // TODO: Integrate with backend
            Toast.makeText(this, "Announcement sent to $sendTo.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun previewAnnouncement() {
        val title = inputTitle.text.toString().trim()
        val message = inputMessage.text.toString().trim()
        val sendTo = if (rbAllStudents.isChecked) "All Students" else "Entire Class"
        Toast.makeText(this, "Preview:\n$title\n$message\nSend to: $sendTo", Toast.LENGTH_LONG).show()
        // You can launch a real preview screen here if desired.
    }
}
