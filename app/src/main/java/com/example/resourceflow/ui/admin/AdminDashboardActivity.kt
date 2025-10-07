package com.example.resourceflow.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R
import com.example.resourceflow.network.AppConfig
import com.example.resourceflow.ui.admin.resourcemanage.ClassroomListActivity
import com.example.resourceflow.ui.auth.LoginActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Data classes matching your PHP JSON response structure
data class User(
    val name: String,
    val email: String,
    val role: String
)

data class DashboardStats(
    val status: String,
    val user: User,
    val total_rooms: Int,
    val total_faculty: Int,
    val total_students: Int,
    val total_requests: Int
)

// Retrofit API interface
interface AdminApiService {
    @GET("admin_dashboard.php")
    fun getDashboardStats(@Query("email") email: String): Call<DashboardStats>
}

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var textGreeting: TextView
    private lateinit var textRoomCount: TextView
    private lateinit var textPendingCount: TextView

    private var userName: String = ""
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Get user data from intent
        userName = intent.getStringExtra("USER_NAME") ?: "Admin"
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        initializeViews()
        setupClickListeners()

        // Set greeting immediately using passed name
        textGreeting.text = "Hello $userName"

        // Fetch updated stats using email
        fetchDashboardStats(userEmail)
    }

    private fun initializeViews() {
        textGreeting = findViewById(R.id.textGreeting)
        textRoomCount = findViewById(R.id.textRoomCount)
        textPendingCount = findViewById(R.id.textPendingCount)
    }

    private fun setupClickListeners() {
        findViewById<LinearLayout>(R.id.btnUserManagement).setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.btnResourceManagement).setOnClickListener {
            startActivity(Intent(this, ClassroomListActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.btnReports).setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnAnnouncements).setOnClickListener {
            startActivity(Intent(this, AnnouncementsActivity::class.java))
        }
        findViewById<Button>(R.id.btnAddResource).setOnClickListener {
            startActivity(Intent(this, AddRoomActivity::class.java))
        }
        findViewById<Button>(R.id.profileIcon).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun fetchDashboardStats(email: String) {
        if (email.isBlank()) {
            showError("Missing email to fetch data")
            return
        }

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)  // e.g., "http://192.168.1.100/resourceflow/"
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(AdminApiService::class.java)

        apiService.getDashboardStats(email).enqueue(object : Callback<DashboardStats> {
            override fun onResponse(call: Call<DashboardStats>, response: Response<DashboardStats>) {
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!

                    // Update greeting if API returns more accurate name
                    textGreeting.text = "Hello ${stats.user.name}"

                    // Update stats
                    textRoomCount.text = stats.total_rooms.toString()
                    textPendingCount.text = stats.total_requests.toString()

                } else {
                    showError("Failed to load dashboard statistics")
                }
            }

            override fun onFailure(call: Call<DashboardStats>, t: Throwable) {
                showError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this@AdminDashboardActivity, message, Toast.LENGTH_LONG).show()
    }
}
