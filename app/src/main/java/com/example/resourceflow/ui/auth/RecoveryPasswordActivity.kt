package com.example.resourceflow.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R
import com.example.resourceflow.network.ApiClient
import com.example.resourceflow.ui.admin.BasicResponse
import com.example.resourceflow.network.ResetWithOtpRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecoveryPasswordActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnSendEmail: Button
    private lateinit var inputOtp: EditText
    private lateinit var inputNewPassword: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery_password)

        btnBack = findViewById(R.id.btnBack)
        btnSendEmail = findViewById(R.id.btnSendEmail)
        inputOtp = findViewById(R.id.inputOtp)
        inputNewPassword = findViewById(R.id.inputNewPassword)
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword)
        tvLogin = findViewById(R.id.tvLogin)

        // Back button finishes this activity
        btnBack.setOnClickListener {
            finish()
        }

        // Login text navigates to LoginActivity
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Get email passed from previous activity
        val email = intent.getStringExtra("EMAIL") ?: ""

        btnSendEmail.setOnClickListener {
            val otp = inputOtp.text.toString().trim()
            val newPassword = inputNewPassword.text.toString().trim()
            val confirmPassword = inputConfirmPassword.text.toString().trim()

            // Validate inputs
            if (otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable send button and optionally show loading (add progress bar if needed)
            btnSendEmail.isEnabled = false
            btnSendEmail.text = "Processing..."

            // Create request object matching PHP expects keys: email, otp, new_password
            val request = ResetWithOtpRequest(email, otp, newPassword)

            // Call API to verify OTP and reset password
            ApiClient.apiService.resetPasswordWithOtp(request).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    btnSendEmail.isEnabled = true
                    btnSendEmail.text = "Reset Password"

                    val resp = response.body()
                    if (resp != null && resp.success) {
                        Toast.makeText(this@RecoveryPasswordActivity, "Password reset successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RecoveryPasswordActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@RecoveryPasswordActivity, resp?.message ?: "Reset failed", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    btnSendEmail.isEnabled = true
                    btnSendEmail.text = "Reset Password"
                    Toast.makeText(this@RecoveryPasswordActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
