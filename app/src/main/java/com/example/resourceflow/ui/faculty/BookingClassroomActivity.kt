package com.example.resourceflow.ui.faculty

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.network.ApiClient
import com.example.resourceflow.network.ClassroomDto
import com.example.resourceflow.ui.classroom.ClassroomAdapter
import com.example.resourceflow.ui.classroom.model.toUi
import kotlinx.coroutines.launch

class BookingClassroomActivity : AppCompatActivity() {

    private lateinit var rvClassrooms: RecyclerView
    private lateinit var adapter: ClassroomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom_booking)

        rvClassrooms = findViewById(R.id.rvClassrooms)
        rvClassrooms.layoutManager = LinearLayoutManager(this)

        fetchClassrooms()
    }

    private fun fetchClassrooms() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getClassrooms()
                if (response.isSuccessful) {
                    val body = response.body()
                    val dtos: List<ClassroomDto> = (body?.classrooms ?: emptyList()) as List<ClassroomDto>
                    val uiList = dtos.map { it.toUi() }

                    adapter = ClassroomAdapter(uiList) { ui ->
                        val intent = Intent(this@BookingClassroomActivity, BookingActivity::class.java)
                        intent.putExtra("classroom_id", ui.id)
                        intent.putExtra("classroom_name", ui.name)
                        startActivity(intent)
                    }
                    rvClassrooms.adapter = adapter
                } else {
                    Toast.makeText(
                        this@BookingClassroomActivity,
                        getString(R.string.err_load_classrooms),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@BookingClassroomActivity,
                    getString(R.string.err_generic, e.localizedMessage ?: "Unknown"),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
