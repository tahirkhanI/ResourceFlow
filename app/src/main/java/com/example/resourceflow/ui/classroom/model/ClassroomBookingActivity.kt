package com.example.resourceflow.ui.classroom

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.network.ApiClient
import com.example.resourceflow.network.ClassroomDto
import com.example.resourceflow.ui.classroom.model.toUi
import com.example.resourceflow.ui.classroom.model.ClassroomUi
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class ClassroomBookingActivity : AppCompatActivity() {

    private lateinit var rvClassrooms: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var chipGroupCategories: ChipGroup

    private var classrooms: List<ClassroomUi> = emptyList()
    private var filteredClassrooms: List<ClassroomUi> = emptyList()
    private lateinit var adapter: ClassroomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom_booking)

        rvClassrooms = findViewById(R.id.rvClassrooms)
        etSearch = findViewById(R.id.etSearch)
        chipGroupCategories = findViewById(R.id.chipGroupCategories)

        rvClassrooms.layoutManager = LinearLayoutManager(this)
        adapter = ClassroomAdapter(emptyList()) { classroomUi ->
            Toast.makeText(this, "Book clicked for ${classroomUi.name}", Toast.LENGTH_SHORT).show()
            // Navigate if needed; pass only primitives (id, name)
        }
        rvClassrooms.adapter = adapter

        fetchClassrooms()
        setupSearch()
        setupFilters()
    }

    private fun fetchClassrooms() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getClassrooms()
                if (response.isSuccessful) {
                    val dtoList: List<ClassroomDto> = response.body()?.classrooms ?: emptyList()
                    classrooms = dtoList.map { it.toUi() }
                    filteredClassrooms = classrooms
                    updateList()
                } else {
                    Toast.makeText(this@ClassroomBookingActivity, getString(R.string.err_load_classrooms), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ClassroomBookingActivity, getString(R.string.err_generic, e.localizedMessage ?: "Unknown"), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = filterList()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupFilters() {
        chipGroupCategories.setOnCheckedChangeListener { _, _ -> filterList() }
    }

    private fun filterList() {
        val query = etSearch.text.toString().trim()
        val selectedChip = chipGroupCategories.checkedChipId

        filteredClassrooms = classrooms.filter { c ->
            val matchesQuery = query.isEmpty() || c.name.contains(query, ignoreCase = true)
            val matchesFilter = when (selectedChip) {
                R.id.chipAll -> true
                R.id.chipProjector -> c.hasProjector
                R.id.chipLargeCapacity -> c.capacity >= 50
                else -> true
            }
            matchesQuery && matchesFilter
        }
        updateList()
    }

    private fun updateList() {
        adapter.updateList(filteredClassrooms)
    }
}
