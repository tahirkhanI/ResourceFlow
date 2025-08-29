package com.example.resourceflow.ui.resource

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R

class ResourceRequestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_request)

        val spinnerCourse = findViewById<Spinner>(R.id.spinnerCourse)
        val spinnerRoom = findViewById<Spinner>(R.id.spinnerRoom)
        val etDateTime = findViewById<EditText>(R.id.etDateTime)
        val cbOther = findViewById<CheckBox>(R.id.cbOther)
        val etOtherEquipment = findViewById<EditText>(R.id.etOtherEquipment)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        // Sample course list (replace with backend data)
        val courses = listOf("Select your course", "B.Tech CSE", "MBA", "BBA", "B.Sc Physics")
        spinnerCourse.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, courses)

        // Sample room list (replace with backend data)
        val rooms = listOf("Select room", "Room 201", "Lab 1", "Room 305", "Conf Hall")
        spinnerRoom.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, rooms)

        // Show 'Other' equipment textbox only if checked
        cbOther.setOnCheckedChangeListener { _, isChecked ->
            etOtherEquipment.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Submit logic (add real integration here)
        btnSubmit.setOnClickListener {
            val course = spinnerCourse.selectedItem?.toString() ?: ""
            val dateTime = etDateTime.text.toString()
            val equipment = mutableListOf<String>()
            if (findViewById<CheckBox>(R.id.cbProjector).isChecked) equipment.add("Projector")
            if (findViewById<CheckBox>(R.id.cbComputer).isChecked) equipment.add("Computer")
            if (findViewById<CheckBox>(R.id.cbAudio).isChecked) equipment.add("Audio System")
            if (findViewById<CheckBox>(R.id.cbVC).isChecked) equipment.add("Video Conference Equipment")
            if (findViewById<CheckBox>(R.id.cbWhiteboard).isChecked) equipment.add("Whiteboard")
            if (cbOther.isChecked) equipment.add("Other: ${etOtherEquipment.text}")

            val room = spinnerRoom.selectedItem?.toString() ?: ""
            val notes = findViewById<EditText>(R.id.etNotes).text.toString()

            // TODO: Replace with backend integration
            Toast.makeText(
                this,
                "Request submitted for: $course on $dateTime\nRoom: $room\nEquipment: ${equipment.joinToString()}\nNotes: $notes",
                Toast.LENGTH_LONG
            ).show()

            // Clear or finish as desired
        }
    }
}
