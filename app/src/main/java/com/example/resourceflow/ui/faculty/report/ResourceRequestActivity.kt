package com.example.resourceflow.ui.faculty.report


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R
import com.example.resourceflow.network.ApiService
import com.example.resourceflow.network.AppConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST



class ResourceRequestActivity : AppCompatActivity() {

    private lateinit var cbProjector: CheckBox
    private lateinit var cbComputer: CheckBox
    private lateinit var cbAudio: CheckBox
    private lateinit var cbVC: CheckBox
    private lateinit var cbWhiteboard: CheckBox
    private lateinit var cbOther: CheckBox
    private lateinit var etOtherEquipment: EditText
    private lateinit var etRoomNumber: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_request) // Replace with your XML layout name

        // Initialize views
        cbProjector = findViewById(R.id.cbProjector)
        cbComputer = findViewById(R.id.cbComputer)
        cbAudio = findViewById(R.id.cbAudio)
        cbVC = findViewById(R.id.cbVC)
        cbWhiteboard = findViewById(R.id.cbWhiteboard)
        cbOther = findViewById(R.id.cbOther)
        etOtherEquipment = findViewById(R.id.etOtherEquipment)
        etRoomNumber = findViewById(R.id.etRoomNumber)
        etNotes = findViewById(R.id.etNotes)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Show/hide other equipment field based on checkbox
        cbOther.setOnCheckedChangeListener { _, isChecked ->
            etOtherEquipment.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Submit button click listener
        btnSubmit.setOnClickListener {
            submitReport()
        }
    }

    private fun submitReport() {
        // Collect selected equipment
        val equipmentList = mutableListOf<String>()
        if (cbProjector.isChecked) equipmentList.add("Projector")
        if (cbComputer.isChecked) equipmentList.add("Computer")
        if (cbAudio.isChecked) equipmentList.add("Audio System")
        if (cbVC.isChecked) equipmentList.add("Video Conference Equipment")
        if (cbWhiteboard.isChecked) equipmentList.add("Whiteboard")
        if (cbOther.isChecked && etOtherEquipment.text.isNotBlank()) {
            equipmentList.add(etOtherEquipment.text.toString())
        }

        // Build problem description
        val problemDescription = buildString {
            append("Requested Equipment: ${if (equipmentList.isNotEmpty()) equipmentList.joinToString(", ") else "None"}")
            val notes = etNotes.text.toString().trim()
            if (notes.isNotEmpty()) {
                append("\nAdditional Notes: $notes")
            }
        }

        // Get room number
        val classroomNumber = etRoomNumber.text.toString().trim()

        // Validate input
        if (classroomNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a room number", Toast.LENGTH_SHORT).show()
            return
        }
        if (equipmentList.isEmpty() && etNotes.text.isBlank()) {
            Toast.makeText(this, "Please select at least one equipment or add notes", Toast.LENGTH_SHORT).show()
            return
        }

        // Assume reporter_id is obtained from user session (replace with actual logic)
        val reporterId = 1 // Replace with actual user ID from your app's authentication system

        // Create report request
        val report = ReportRequest(
            reporter_id = reporterId,
            classroom_number = classroomNumber,
            problem_description = problemDescription
        )

        // Set up Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL) // Replace with your server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService.ReportApi::class.java)

        // Send request
        api.submitReport(report).enqueue(object : Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (response.isSuccessful) {
                    val reportResponse = response.body()
                    Toast.makeText(this@ResourceRequestActivity, reportResponse?.message ?: "Report submitted", Toast.LENGTH_LONG).show()
                    finish() // Close activity on success
                } else {
                    Toast.makeText(this@ResourceRequestActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Toast.makeText(this@ResourceRequestActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}