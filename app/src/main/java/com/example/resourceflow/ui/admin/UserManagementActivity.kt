package com.example.resourceflow.ui.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resourceflow.R
import com.example.resourceflow.adapter.UserAdapter
import com.example.resourceflow.api.UserApiService
import com.example.resourceflow.model.User
import com.example.resourceflow.network.AppConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class UserManagementActivity : AppCompatActivity(), UserAdapter.OnUserSelectionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val allUsers = mutableListOf<User>()
    private val filteredUsers = mutableListOf<User>()
    private val selectedUsers = mutableSetOf<Int>()

    // UI Elements
    private lateinit var editSearch: EditText
    private lateinit var btnClearSearch: ImageButton
    private lateinit var btnFilterAll: Button
    private lateinit var btnFilterStudent: Button
    private lateinit var btnFilterFaculty: Button
    private lateinit var btnFilterAdmin: Button
    private lateinit var btnAddUser: ImageButton
    private lateinit var btnDeleteUser: Button
    private lateinit var deleteBar: View

    private var currentRoleFilter: String = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerUsers)
        editSearch = findViewById(R.id.editSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        btnFilterAll = findViewById(R.id.btnFilterAll)
        btnFilterStudent = findViewById(R.id.btnFilterStudent)
        btnFilterFaculty = findViewById(R.id.btnFilterFaculty)
        btnFilterAdmin = findViewById(R.id.btnFilterAdmin)
        btnAddUser = findViewById(R.id.btnAddUser)
        btnDeleteUser = findViewById(R.id.btnDeleteUser)
        deleteBar = findViewById(R.id.deleteBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(filteredUsers, this)
        recyclerView.adapter = userAdapter

        btnDeleteUser.isEnabled = false
        deleteBar.visibility = View.GONE
        btnClearSearch.visibility = View.GONE

        setupListeners()
        fetchUsers()
    }

    private fun setupListeners() {
        // Search text logic
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                btnClearSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
                filterUsers(query, currentRoleFilter)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        btnClearSearch.setOnClickListener { editSearch.text.clear() }

        // Filter role buttons
        btnFilterAll.setOnClickListener { setRoleFilter("all") }
        btnFilterStudent.setOnClickListener { setRoleFilter("student") }
        btnFilterFaculty.setOnClickListener { setRoleFilter("faculty") }
        btnFilterAdmin.setOnClickListener { setRoleFilter("admin") }

        // Add User button (launch actual activity as needed)
        btnAddUser.setOnClickListener {
            Toast.makeText(this, "Add User – implement navigation", Toast.LENGTH_SHORT).show()
        }

        // Delete button (demo delete toast)
        btnDeleteUser.setOnClickListener {
            if (selectedUsers.isEmpty()) {
                Toast.makeText(this, "Select users to delete", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Delete ${selectedUsers.size} users (not implemented)", Toast.LENGTH_SHORT).show()
                // TODO: Implement delete users from backend and refresh
            }
        }
    }

    private fun setRoleFilter(role: String) {
        currentRoleFilter = role
        val selectedBg = ContextCompat.getColor(this, R.color.purple_500)
        val normalBg = ContextCompat.getColor(this, R.color.gray_light)
        val selectedTxt = ContextCompat.getColor(this, android.R.color.white)
        val normalTxt = ContextCompat.getColor(this, android.R.color.black)

        // Set background and text colors for each filter button
        btnFilterAll.setBackgroundColor(if (role == "all") selectedBg else normalBg)
        btnFilterAll.setTextColor(if (role == "all") selectedTxt else normalTxt)

        btnFilterStudent.setBackgroundColor(if (role == "student") selectedBg else normalBg)
        btnFilterStudent.setTextColor(if (role == "student") selectedTxt else normalTxt)

        btnFilterFaculty.setBackgroundColor(if (role == "faculty") selectedBg else normalBg)
        btnFilterFaculty.setTextColor(if (role == "faculty") selectedTxt else normalTxt)

        btnFilterAdmin.setBackgroundColor(if (role == "admin") selectedBg else normalBg)
        btnFilterAdmin.setTextColor(if (role == "admin") selectedTxt else normalTxt)

        filterUsers(editSearch.text.toString(), role)
    }

    private fun filterUsers(query: String, roleFilter: String) {
        val lowerQuery = query.lowercase()
        filteredUsers.clear()
        filteredUsers.addAll(allUsers.filter { user ->
            val matchesQuery = user.name.lowercase().contains(lowerQuery) || user.email.lowercase().contains(lowerQuery)
            val matchesRole = when (roleFilter) {
                "student" -> user.role.equals("student", true)
                "faculty" -> user.role.equals("faculty", true)
                "admin" -> user.role.equals("admin", true)
                else -> true
            }
            matchesQuery && matchesRole
        })
        userAdapter.clearSelection()
        userAdapter.notifyDataSetChanged()
        updateDeleteBar()
    }

    private fun fetchUsers() {
        allUsers.clear()
        filteredUsers.clear()
        selectedUsers.clear()
        deleteBar.visibility = View.GONE
        btnDeleteUser.isEnabled = false

        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL) // replace with your backend!
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(UserApiService::class.java)
        api.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body() != null) {
                    allUsers.addAll(response.body()!!)
                    setRoleFilter("all")
                } else {
                    Toast.makeText(this@UserManagementActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@UserManagementActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- Adapter selection callbacks ---
    override fun onUserSelected(userId: Int) {
        selectedUsers.add(userId)
        updateDeleteBar()
    }
    override fun onUserDeselected(userId: Int) {
        selectedUsers.remove(userId)
        updateDeleteBar()
    }

    private fun updateDeleteBar() {
        if (selectedUsers.isEmpty()) {
            deleteBar.visibility = View.GONE
            btnDeleteUser.isEnabled = false
            btnDeleteUser.text = "Delete"
        } else {
            deleteBar.visibility = View.VISIBLE
            btnDeleteUser.isEnabled = true
            btnDeleteUser.text = "Delete (${selectedUsers.size})"
        }
    }
}
