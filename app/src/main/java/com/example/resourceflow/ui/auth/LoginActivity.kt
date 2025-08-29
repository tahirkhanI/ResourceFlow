package com.example.resourceflow.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R
import com.example.resourceflow.network.ApiClient
import com.example.resourceflow.network.ForgetPasswordActivity
import com.example.resourceflow.network.LoginRequest
import com.example.resourceflow.network.LoginResponse
import com.example.resourceflow.ui.faculty.FacultyDashboardActivity
import com.example.resourceflow.ui.student.StudentDashboardActivity
import com.example.resourceflow.ui.admin.AdminDashboardActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Find views
        val signUp = findViewById<TextView>(R.id.signUp)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val togglePassword = findViewById<ImageView>(R.id.togglePassword)
        val tvForgetPassword = findViewById<TextView>(R.id.tvForgetPassword)

        // Forget Password click
        tvForgetPassword.setOnClickListener {
            // Navigate to ForgetPasswordActivity
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }

        // Password show/hide toggle
        togglePassword.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                inputPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_eye_off)
            } else {
                inputPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_eye)
            }
            inputPassword.setSelection(inputPassword.text?.length ?: 0)
        }

        // Signup click
        signUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Login button click
        btnLogin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, password)
            ApiClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val resp = response.body()
                    if (resp?.status == "success" && !resp.role.isNullOrBlank()) {
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        val user = resp.user

                        val intent = when (resp.role.lowercase()) {
                            "faculty" -> Intent(this@LoginActivity, FacultyDashboardActivity::class.java)
                            "student" -> Intent(this@LoginActivity, StudentDashboardActivity::class.java)
                            "admin" -> Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                            else -> {
                                Toast.makeText(this@LoginActivity, "Unknown role: ${resp.role}", Toast.LENGTH_LONG).show()
                                return
                            }
                        }

                        // Pass user data to dashboard
                        user?.let {
                            intent.putExtra("USER_ID", it.id)
                            intent.putExtra("USER_NAME", it.name)
                            intent.putExtra("USER_EMAIL", it.email)
                        }

                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this@LoginActivity, resp?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
