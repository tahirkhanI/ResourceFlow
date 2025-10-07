package com.example.resourceflow.ui.faculty.booking


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.network.AppConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

// Data class for booking
data class Booking(
    val id: Int,
    val classroom_id: String,
    val booking_date: String,
    val start_time: String,
    val end_time: String,
    val user_id: Int,
    val status: String,
    val created_at: String,
    val updated_at: String
)

// Data class for booking list response
data class BookingListResponse(
    val success: Boolean,
    val bookings: List<Booking>? = null,
    val error: String? = null
)

// Data class for unbook response
data class UnbookResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

// Data class for unbook request
data class UnbookRequest(
    val booking_id: Int
)

// Retrofit API interface
interface BookingApi {
    @GET("get_user_bookings.php")
    fun getUserBookings(@Query("user_id") userId: Int): Call<BookingListResponse>

    @POST("unbook_classroom.php")
    fun unbookClassroom(@Body request: UnbookRequest): Call<UnbookResponse>
}

// RecyclerView Adapter
class BookingAdapter(
    private val bookings: MutableList<Booking>,
    private val onUnbookClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClassroomId: TextView = itemView.findViewById(R.id.tvClassroomId)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnUnbook: Button = itemView.findViewById(R.id.btnUnbook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvClassroomId.text = "Classroom: ${booking.classroom_id}"
        holder.tvDate.text = "Date: ${booking.booking_date}"
        holder.tvTime.text = "Time: ${booking.start_time} - ${booking.end_time}"
        holder.tvStatus.text = "Status: ${booking.status}"
        holder.btnUnbook.setOnClickListener { onUnbookClick(booking) }
    }

    override fun getItemCount(): Int = bookings.size

    fun updateBookings(newBookings: List<Booking>) {
        bookings.clear()
        bookings.addAll(newBookings)
        notifyDataSetChanged()
    }
}

class UserBookingsActivity : AppCompatActivity() {

    private lateinit var rvBookings: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoBookings: TextView
    private lateinit var adapter: BookingAdapter
    private val bookings = mutableListOf<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_bookings)

        // Initialize views
        rvBookings = findViewById(R.id.rvBookings)
        progressBar = findViewById(R.id.progressBar)
        tvNoBookings = findViewById(R.id.tvNoBookings)

        // Set up RecyclerView
        adapter = BookingAdapter(bookings) { booking ->
            unbookClassroom(booking.id)
        }
        rvBookings.layoutManager = LinearLayoutManager(this)
        rvBookings.adapter = adapter

        // Fetch bookings
        fetchUserBookings()
    }

    private fun fetchUserBookings() {
        // Assume user_id is obtained from authentication system
        val userId = 1 // Replace with actual user ID from SharedPreferences or auth system

        progressBar.visibility = View.VISIBLE
        tvNoBookings.visibility = View.GONE
        rvBookings.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL) // Replace with your XAMPP server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(BookingApi::class.java)
        api.getUserBookings(userId).enqueue(object : Callback<BookingListResponse> {
            override fun onResponse(call: Call<BookingListResponse>, response: Response<BookingListResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val bookingResponse = response.body()
                    if (bookingResponse?.success == true && bookingResponse.bookings != null) {
                        if (bookingResponse.bookings.isEmpty()) {
                            tvNoBookings.visibility = View.VISIBLE
                            rvBookings.visibility = View.GONE
                        } else {
                            tvNoBookings.visibility = View.GONE
                            rvBookings.visibility = View.VISIBLE
                            adapter.updateBookings(bookingResponse.bookings)
                        }
                    } else {
                        Toast.makeText(this@UserBookingsActivity, bookingResponse?.error ?: "Error fetching bookings", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@UserBookingsActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BookingListResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@UserBookingsActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun unbookClassroom(bookingId: Int) {
        progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL) // Replace with your XAMPP server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(BookingApi::class.java)
        val request = UnbookRequest(bookingId)
        api.unbookClassroom(request).enqueue(object : Callback<UnbookResponse> {
            override fun onResponse(call: Call<UnbookResponse>, response: Response<UnbookResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val unbookResponse = response.body()
                    if (unbookResponse?.success == true) {
                        Toast.makeText(this@UserBookingsActivity, unbookResponse.message ?: "Booking cancelled", Toast.LENGTH_LONG).show()
                        fetchUserBookings() // Refresh the list
                    } else {
                        Toast.makeText(this@UserBookingsActivity, unbookResponse?.error ?: "Error cancelling booking", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@UserBookingsActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UnbookResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@UserBookingsActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}