package com.example.resourceflow.ui.faculty

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var tvBookingTitle: TextView
    private lateinit var tvSelectedRoom: TextView
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectTime: Button
    private lateinit var tvChosenDateTime: TextView
    private lateinit var btnConfirmBooking: Button

    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        tvBookingTitle = findViewById(R.id.tvBookingTitle)
        tvSelectedRoom = findViewById(R.id.tvSelectedRoom)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnSelectTime = findViewById(R.id.btnSelectTime)
        tvChosenDateTime = findViewById(R.id.tvChosenDateTime)
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking)

        val classroomName = intent.getStringExtra("classroom_name") ?: "Unknown Room"

        tvBookingTitle.text = "Booking for room: $classroomName"
        tvSelectedRoom.text = "Selected Room: $classroomName"

        btnSelectDate.setOnClickListener { openDatePicker() }
        btnSelectTime.setOnClickListener { openTimePicker() }
        btnConfirmBooking.setOnClickListener {
            if (selectedDate != null && selectedTime != null) {
                Toast.makeText(
                    this,
                    "Booked $classroomName on $selectedDate at $selectedTime",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedCal = Calendar.getInstance()
            selectedCal.set(year, month, dayOfMonth)
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selectedCal.time)
            updateDateTimeText()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun openTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            val selectedCal = Calendar.getInstance()
            selectedCal.set(Calendar.HOUR_OF_DAY, hour)
            selectedCal.set(Calendar.MINUTE, minute)
            selectedTime = SimpleDateFormat("HH:mm", Locale.US).format(selectedCal.time)
            updateDateTimeText()
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    private fun updateDateTimeText() {
        tvChosenDateTime.text = when {
            selectedDate != null && selectedTime != null -> "Selected: $selectedDate at $selectedTime"
            selectedDate != null -> "Selected date: $selectedDate"
            selectedTime != null -> "Selected time: $selectedTime"
            else -> "No date and time selected"
        }
    }
}
