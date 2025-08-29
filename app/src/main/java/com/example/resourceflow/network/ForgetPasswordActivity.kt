package com.example.resourceflow.network

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resourceflow.R
import com.example.resourceflow.ui.auth.LoginActivity
import com.example.resourceflow.ui.auth.RecoveryPasswordActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnSendEmail: Button
    private lateinit var inputEmail: EditText
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        btnBack = findViewById(R.id.btnBack)
        btnSendEmail = findViewById(R.id.btnSendEmail)
        inputEmail = findViewById(R.id.inputEmail)
        tvLogin = findViewById(R.id.tvLogin)
        progressBar = findViewById(R.id.progressBar)

        btnBack.setOnClickListener {
            finish()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        inputEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                btnSendEmail.performClick()
                true
            } else {
                false
            }
        }

        btnSendEmail.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            if (email.isEmpty()) {
                inputEmail.error = "Please enter your email."
                inputEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmail.error = "Invalid email address."
                inputEmail.requestFocus()
                return@setOnClickListener
            }

            sendOtp(email)
        }
    }

    private fun sendOtp(email: String) {
        btnSendEmail.isEnabled = false
        btnSendEmail.text = "Sending..."
        progressBar.visibility = ProgressBar.VISIBLE

        val request = ForgotPasswordOtpRequest(email)
        ApiClient.apiService.sendOtpToMail(request).enqueue(object :
            Callback<ForgotPasswordOtpResponse> {
            override fun onResponse(
                call: Call<ForgotPasswordOtpResponse>,
                response: Response<ForgotPasswordOtpResponse>
            ) {
                btnSendEmail.isEnabled = true
                btnSendEmail.text = "Send Email"
                progressBar.visibility = ProgressBar.GONE

                if (response.isSuccessful) {
                    val resp = response.body()
                    if (resp?.status == "success") {
                        Toast.makeText(this@ForgetPasswordActivity, resp.message ?: "OTP sent!", Toast.LENGTH_SHORT).show()
                        // Proceed to next screen with the email
                        val intent = Intent(
                            this@ForgetPasswordActivity,
                            RecoveryPasswordActivity::class.java
                        )
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                        finish()
                    } else if (resp?.message != null && resp.message.contains("No matching account exists", ignoreCase = true)) {
                        // Specific check for no account found message
                        Toast.makeText(this@ForgetPasswordActivity, resp.message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@ForgetPasswordActivity, resp?.message ?: "Failed to send OTP.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // You can check if server sends 404 (Not Found) for email validation failure
                    if (response.code() == 404) {
                        Toast.makeText(this@ForgetPasswordActivity, "No account found with this email.", Toast.LENGTH_LONG).show()
                    } else {
                        val errorMsg = response.errorBody()?.string()
                        Toast.makeText(this@ForgetPasswordActivity, errorMsg ?: "Server error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ForgotPasswordOtpResponse>, t: Throwable) {
                btnSendEmail.isEnabled = true
                btnSendEmail.text = "Send Email"
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this@ForgetPasswordActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}