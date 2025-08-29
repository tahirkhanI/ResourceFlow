package com.example.resourceflow.ui.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R



class AddResourceActivity : AppCompatActivity() {

    private lateinit var etSearchRoom: EditText
    private lateinit var spRoomType: Spinner
    private lateinit var spFloor: Spinner
    private lateinit var etRoomNumber: EditText
    private lateinit var etBlockName: EditText
    private lateinit var cbProjector: CheckBox
    private lateinit var cbComputer: CheckBox
    private lateinit var cbWhiteboard: CheckBox
    private lateinit var cbAC: CheckBox
    private lateinit var cbSpeakers: CheckBox
    private lateinit var cbSmartBoard: CheckBox
    private lateinit var btnSaveRoom: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_resource)  // Ensure this matches your XML filename

        etSearchRoom = findViewById(R.id.etSearchRoom)
        spRoomType = findViewById(R.id.spRoomType)
        spFloor = findViewById(R.id.spFloor)
        etRoomNumber = findViewById(R.id.etRoomNumber)
        etBlockName = findViewById(R.id.etBlockName)
        cbProjector = findViewById(R.id.cbProjector)
        cbComputer = findViewById(R.id.cbComputer)
        cbWhiteboard = findViewById(R.id.cbWhiteboard)
        cbAC = findViewById(R.id.cbAC)
        cbSpeakers = findViewById(R.id.cbSpeakers)
        cbSmartBoard = findViewById(R.id.cbSmartBoard)
        btnSaveRoom = findViewById(R.id.btnSaveRoom)

        setupSpinners()
        setupSearchListener()
        setupSaveButton()
    }

    private fun setupSpinners() {
        val roomTypes = listOf("Lecture Hall", "Lab", "Seminar Room", "Conference Room")
        val roomTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roomTypes)
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRoomType.adapter = roomTypeAdapter

        val floors = listOf("1", "2", "3", "4", "5")
        val floorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, floors)
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFloor.adapter = floorAdapter
    }

    private fun setupSearchListener() {
        etSearchRoom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                // TODO: Implement search filtering
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupSaveButton() {
        btnSaveRoom.setOnClickListener {
            val roomNumber = etRoomNumber.text.toString().trim()
            val blockName = etBlockName.text.toString().trim()
            val roomType = spRoomType.selectedItem?.toString() ?: ""
            val floor = spFloor.selectedItem?.toString() ?: ""

            val selectedResources = mutableListOf<String>()
            if (cbProjector.isChecked) selectedResources.add("Projector")
            if (cbComputer.isChecked) selectedResources.add("Computer")
            if (cbWhiteboard.isChecked) selectedResources.add("Whiteboard")
            if (cbAC.isChecked) selectedResources.add("AC")
            if (cbSpeakers.isChecked) selectedResources.add("Speakers")
            if (cbSmartBoard.isChecked) selectedResources.add("Smart Board")

            if (roomNumber.isEmpty()) {
                Toast.makeText(this, "Please enter room number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (blockName.isEmpty()) {
                Toast.makeText(this, "Please enter block name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Replace with actual saving logic

            Toast.makeText(
                this,
                "Saved Room: $roomNumber, Block: $blockName, Type: $roomType, Floor: $floor\nResources: ${selectedResources.joinToString()}",
                Toast.LENGTH_LONG
            ).show()

            // Optional: Clear form or finish activity
        }
    }
}
