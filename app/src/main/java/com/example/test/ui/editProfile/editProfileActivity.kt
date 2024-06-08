package com.example.test.ui.editProfile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.example.test.data.pref.UserModel
import com.example.test.data.response.LoginResponse
import com.example.test.data.response.UpdateProfileResponse
import com.example.test.data.retrofit.ApiConfig
import com.example.test.databinding.ActivityEditProfileBinding
import com.example.test.ui.ViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class editProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private val viewModel by viewModels<EditViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")

        binding.editUsername.setText(username)
        binding.editEmail.setText(email)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        viewModel.getSession().observe(this) { user ->
            if (user != null && user.isLogin) {
                val token = user.token
                val userId = user.uid

                binding.saveButton.setOnClickListener {
                    val newUsername = binding.editUsername.text.toString()
                    val newEmail = binding.editEmail.text.toString()
                    updateUserProfile(token, userId, newUsername, newEmail)
                }
            }
        }
    }

    private fun updateUserProfile(token: String, uid: String, name: String, email: String) {
        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.updateUser("Bearer $token", uid, mapOf("name" to name, "email" to email))
                if (response.isSuccessful) {
                    val successResponse = response.body()?.message
                    val updatedUser = response.body()?.dataUpdate

                    successResponse?.let { showToast(it) }

                    if (updatedUser != null) {
                        viewModel.saveSession(UserModel(updatedUser.email ?: "", token, true, updatedUser.name ?: "", updatedUser.userId ?: ""))
                    }
                    showToast(getString(R.string.success_profile_update))
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, UpdateProfileResponse::class.java)
                    errorResponse.message?.let { showToast(it) }
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, UpdateProfileResponse::class.java)
                errorResponse.message?.let { showToast(it) }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@editProfileActivity, message, Toast.LENGTH_SHORT).show()
    }
}
