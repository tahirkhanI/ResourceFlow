package com.example.resourceflow.ui.faculty.booking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.network.AppConfig
import com.example.resourceflow.ui.admin.resourcemanage.ResourceAdapter
import com.example.resourceflow.ui.admin.resourcemanage.ResourceApi
import com.example.resourceflow.ui.admin.resourcemanage.ResourceListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

data class BookingRequest(
    val classroom_id: Int,
    val booking_date: String,
    val start_time: String,
    val end_time: String,
    val user_id: Int
)

data class BookingResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

interface BookingDetailsApi {
    @POST("book_classroom_api.php")
    fun bookClassroom(@Body request: BookingRequest): Call<BookingResponse>
}

class ClassroomBookingDetailsActivity : AppCompatActivity() {

    private lateinit var tvRoomNumber: TextView
    private lateinit var rvResources: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoResources: TextView
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectStartTime: Button
    private lateinit var btnSelectEndTime: Button
    private lateinit var btnBook: Button
    private var selectedDate: String? = null
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom_booking_details)

        tvRoomNumber = findViewById(R.id.tvRoomNumber)
        rvResources = findViewById(R.id.rvResources)
        progressBar = findViewById(R.id.progressBar)
        tvNoResources = findViewById(R.id.tvNoResources)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime)
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime)
        btnBook = findViewById(R.id.btnBook)

        val classroomId = intent.getIntExtra("classroom_id", 0)
        val roomNumber = intent.getStringExtra("room_number")
        tvRoomNumber.text = "Room: $roomNumber"

        rvResources.layoutManager = LinearLayoutManager(this)

        setupDateTimePickers()
        fetchResources(classroomId)

        btnBook.setOnClickListener {
            bookClassroom(classroomId)
        }
    }

    private fun setupDateTimePickers() {
        val calendar = Calendar.getInstance()

        btnSelectDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                btnSelectDate.text = selectedDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSelectStartTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                selectedStartTime = String.format("%02d:%02d:00", hour, minute)
                btnSelectStartTime.text = selectedStartTime
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        btnSelectEndTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                selectedEndTime = String.format("%02d:%02d:00", hour, minute)
                btnSelectEndTime.text = selectedEndTime
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    private fun fetchResources(classroomId: Int) {
        progressBar.visibility = View.VISIBLE
        tvNoResources.visibility = View.GONE
        rvResources.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ResourceApi::class.java)
        api.getClassroomResources(classroomId).enqueue(object : Callback<ResourceListResponse> {
            override fun onResponse(call: Call<ResourceListResponse>, response: Response<ResourceListResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val resourceResponse = response.body()
                    if (resourceResponse?.success == true && resourceResponse.resources != null) {
                        if (resourceResponse.resources.isEmpty()) {
                            tvNoResources.visibility = View.VISIBLE
                            rvResources.visibility = View.GONE
                        } else {
                            tvNoResources.visibility = View.GONE
                            rvResources.visibility = View.VISIBLE
                            // Convert List<Resource> to MutableList<Resource>
                            rvResources.adapter =
                                ResourceAdapter(resourceResponse.resources.toMutableList()) { _, _ -> }
                        }
                    } else {
                        Toast.makeText(this@ClassroomBookingDetailsActivity, resourceResponse?.error ?: "Error fetching resources", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ClassroomBookingDetailsActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResourceListResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ClassroomBookingDetailsActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun bookClassroom(classroomId: Int) {
        if (selectedDate == null || selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(BookingDetailsApi::class.java)
        val request = BookingRequest(
            classroom_id = classroomId,
            booking_date = selectedDate!!,
            start_time = selectedStartTime!!,
            end_time = selectedEndTime!!,
            user_id = 1 // Assuming a default user_id, replace with actual user_id from login
        )

        api.bookClassroom(request).enqueue(object : Callback<BookingResponse> {
            override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                if (response.isSuccessful) {
                    val bookingResponse = response.body()
                    if (bookingResponse?.success == true) {
                        Toast.makeText(this@ClassroomBookingDetailsActivity, bookingResponse.message ?: "Booking successful", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@ClassroomBookingDetailsActivity, bookingResponse?.error ?: "Booking failed", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ClassroomBookingDetailsActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                Toast.makeText(this@ClassroomBookingDetailsActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}