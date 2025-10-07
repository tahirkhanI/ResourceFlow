package com.example.resourceflow.ui.admin.resourcemanage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.network.AppConfig
import com.example.resourceflow.ui.faculty.booking.Classroom
import com.example.resourceflow.ui.faculty.booking.ClassroomListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Data class for classroom
data class Classroom(
    val id: Int,
    val room_number: String,
    val room_type: String,
    val floor: String,
    val block_name: String
)

// Data class for classroom list response
data class ClassroomListResponse(
    val success: Boolean,
    val classrooms: List<Classroom>? = null,
    val error: String? = null
)

// Retrofit API interface
interface ClassroomApi {
    @GET("getclassrooms.php")
    fun getClassrooms(): Call<ClassroomListResponse>
}

// RecyclerView Adapter for classrooms
class ClassroomAdapter(
    private val classrooms: MutableList<Classroom>,
    private val onClassroomClick: (Classroom) -> Unit
) : RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder>() {

    class ClassroomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoomNumber: TextView = itemView.findViewById(R.id.tvRoomNumber)
        val tvRoomType: TextView = itemView.findViewById(R.id.tvRoomType)
        val tvFloor: TextView = itemView.findViewById(R.id.tvFloor)
        val tvBlockName: TextView = itemView.findViewById(R.id.tvBlockName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_classroom_admin, parent, false)
        return ClassroomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassroomViewHolder, position: Int) {
        val classroom = classrooms[position]
        holder.tvRoomNumber.text = "Room: ${classroom.room_number}"
        holder.tvRoomType.text = "Type: ${classroom.room_type}"
        holder.tvFloor.text = "Floor: ${classroom.floor}"
        holder.tvBlockName.text = "Block: ${classroom.block_name}"
        holder.itemView.setOnClickListener { onClassroomClick(classroom) }
    }

    override fun getItemCount(): Int = classrooms.size

    fun updateClassrooms(newClassrooms: List<Classroom>) {
        classrooms.clear()
        classrooms.addAll(newClassrooms)
        notifyDataSetChanged()
    }
}

class ClassroomListActivity : AppCompatActivity() {

    private lateinit var rvClassrooms: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoClassrooms: TextView
    private lateinit var adapter: ClassroomAdapter
    private val classrooms = mutableListOf<Classroom>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom_list)

        // Initialize views
        rvClassrooms = findViewById(R.id.rvClassrooms)
        progressBar = findViewById(R.id.progressBar)
        tvNoClassrooms = findViewById(R.id.tvNoClassrooms)

        // Set up RecyclerView
        adapter = ClassroomAdapter(classrooms) { classroom ->
            val intent = Intent(this, ClassroomResourcesActivity::class.java)
            intent.putExtra("classroom_id", classroom.id)
            intent.putExtra("room_number", classroom.room_number)
            startActivity(intent)
        }
        rvClassrooms.layoutManager = LinearLayoutManager(this)
        rvClassrooms.adapter = adapter

        // Fetch classrooms
        fetchClassrooms()
    }

    private fun fetchClassrooms() {
        progressBar.visibility = View.VISIBLE
        tvNoClassrooms.visibility = View.GONE
        rvClassrooms.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL) // Replace with your XAMPP server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ClassroomApi::class.java)
        api.getClassrooms().enqueue(object : Callback<ClassroomListResponse> {
            override fun onResponse(call: Call<ClassroomListResponse>, response: Response<ClassroomListResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val classroomResponse = response.body()
                    if (classroomResponse?.success == true && classroomResponse.classrooms != null) {
                        if (classroomResponse.classrooms.isEmpty()) {
                            tvNoClassrooms.visibility = View.VISIBLE
                            rvClassrooms.visibility = View.GONE
                        } else {
                            tvNoClassrooms.visibility = View.GONE
                            rvClassrooms.visibility = View.VISIBLE
                            adapter.updateClassrooms(classroomResponse.classrooms)
                        }
                    } else {
                        Toast.makeText(this@ClassroomListActivity, classroomResponse?.error ?: "Error fetching classrooms", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ClassroomListActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ClassroomListResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ClassroomListActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
