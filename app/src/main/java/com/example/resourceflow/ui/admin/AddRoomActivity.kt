package com.example.resourceflow.ui.admin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R
import com.example.resourceflow.network.AppConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// Data class matching the simple JSON response from PHP backend
data class ApiResponse(
    val status: String,
    val message: String
)

// Retrofit API interface for adding a room
interface RoomApiService {
    @FormUrlEncoded
    @POST("add_room.php")  // Your PHP endpoint
    fun addRoom(
        @Field("room_number") roomNumber: String,
        @Field("room_type") roomType: String,
        @Field("floor") floor: String,
        @Field("block_name") blockName: String
    ): Call<ApiResponse>
}

class AddRoomActivity : AppCompatActivity() {

    private lateinit var editRoomNumber: EditText
    private lateinit var editRoomType: EditText
    private lateinit var editFloor: EditText
    private lateinit var editBlockName: EditText
    private lateinit var btnAddRoom: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room) // Must match your XML filename

        // Initialize views
        editRoomNumber = findViewById(R.id.editRoomNumber)
        editRoomType = findViewById(R.id.editRoomType)
        editFloor = findViewById(R.id.editFloor)
        editBlockName = findViewById(R.id.editBlockName)
        btnAddRoom = findViewById(R.id.btnAddRoom)

        // Set button click listener
        btnAddRoom.setOnClickListener {
            submitRoom()
        }
    }

    private fun submitRoom() {
        val roomNumber = editRoomNumber.text.toString().trim()
        val roomType = editRoomType.text.toString().trim()
        val floor = editFloor.text.toString().trim()
        val blockName = editBlockName.text.toString().trim()

        if (roomNumber.isEmpty() || roomType.isEmpty() || floor.isEmpty() || blockName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        btnAddRoom.isEnabled = false

        // Setup logging for debug (optional)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)  // Replace with your backend URL ending with a slash '/'
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(RoomApiService::class.java)

        apiService.addRoom(roomNumber, roomType, floor, blockName).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                btnAddRoom.isEnabled = true
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    Toast.makeText(this@AddRoomActivity, apiResponse.message, Toast.LENGTH_LONG).show()
                    if (apiResponse.status == "success") {
                        finish() // Close activity on success
                    }
                } else {
                    Toast.makeText(this@AddRoomActivity, "Failed to add room", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                btnAddRoom.isEnabled = true
                Toast.makeText(this@AddRoomActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
