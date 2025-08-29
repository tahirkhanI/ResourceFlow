package com.example.resourceflow.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R
import com.example.resourceflow.network.ApiClient
import com.example.resourceflow.network.SignUpRequest
import com.example.resourceflow.network.SignUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var inputFullName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var rbStudent: RadioButton
    private lateinit var rbFaculty: RadioButton
    private lateinit var rbStaff: RadioButton
    private lateinit var btnCreateAccount: Button
    private lateinit var linkSignIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        inputFullName = findViewById(R.id.inputFullName)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        rbStudent = findViewById(R.id.rbStudent)
        rbFaculty = findViewById(R.id.rbFaculty)
        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        linkSignIn = findViewById(R.id.linkSignIn)

        btnCreateAccount.setOnClickListener {
            val fullName = inputFullName.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val userType = when {
                rbStudent.isChecked -> "student"
                rbFaculty.isChecked -> "faculty"
                rbStaff.isChecked -> "admin" // adjust to "staff" if your backend expects that
                else -> ""
            }

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || userType.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields and select a user type.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- Actual network signup logic ---
            val request = SignUpRequest(
                name = fullName,
                email = email,
                password = password,
                role = userType
            )

            btnCreateAccount.isEnabled = false
            ApiClient.apiService.signup(request).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(
                    call: Call<SignUpResponse>,
                    response: Response<SignUpResponse>
                ) {
                    btnCreateAccount.isEnabled = true
                    val resp = response.body()
                    if (resp?.status == "success") {
                        Toast.makeText(this@SignUpActivity, "Signup successful! Please log in.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignUpActivity, resp?.message ?: "Signup failed.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    btnCreateAccount.isEnabled = true
                    Toast.makeText(this@SignUpActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }

        linkSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
