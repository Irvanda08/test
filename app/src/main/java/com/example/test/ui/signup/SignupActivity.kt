package com.example.test.ui.signup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.example.test.data.response.RegisterResponse
import com.example.test.data.retrofit.ApiConfig
import com.example.test.databinding.ActivitySignupBinding
import com.example.test.ui.login.LoginActivity
import com.example.test.ui.welcome.WelcomeActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.linkLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            showLoading(true)
            val email = binding.tvEmail.text.toString()
            val name = binding.tvUsername.text.toString()
            val password = binding.tvPassword.text.toString()

            if (!isPasswordValid(password)) {
                showToast(getString(R.string.error_password_requirement))
                showLoading(false)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val successResponse = apiService.register(name, email, password).message
                    showToast(successResponse.toString())
                    showLoading(false)
                    // Uncomment the AlertDialog if needed
                    // AlertDialog.Builder(this@SignupActivity).apply {
                    //     setTitle("Yeah!")
                    //     setMessage("Yuk, login")
                    //     setPositiveButton(getString(R.string.next)) { _, _ ->
                    //         finish()
                    //     }
                    //     create()
                    //     show()
                    // }
                    showToast(getString(R.string.success_signup))
                    startActivity(Intent(this@SignupActivity, WelcomeActivity::class.java))
                    finish()
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                    val errorMessage = parseErrorMessage(errorResponse.message)
                    showToast(errorMessage)
                    showLoading(false)
                }
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 && password.any { it.isUpperCase() }
    }

    private fun parseErrorMessage(message: Any?): String {
        return if (message is Map<*, *>) {
            val emailErrors = message["email"] as? List<*>
            emailErrors?.joinToString(", ") ?: "Unknown error"
        } else {
            message?.toString() ?: "Unknown error"
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
