package com.example.test.ui.editPassword

import EditPasswordViewModel
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import com.example.test.databinding.ActivityEditPasswordBinding
import com.example.test.ui.ViewModelFactory

class editPasswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditPasswordBinding

    private val viewModel by viewModels<EditPasswordViewModel>{
        ViewModelFactory.getInstance(this)
    }

    private var token: String = ""
    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    val oldPassword = binding.oldPassword.text.toString()
                    val newPassword = binding.newPassword.text.toString()
                    val confirmPassword = binding.newPassword.text.toString()

                    if (newPassword != confirmPassword) {
                        showToast("New password and confirm password do not match")
                        return@setOnClickListener
                    }

                    if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                        showToast("All fields are required")
                        return@setOnClickListener
                    }

                    viewModel.changePassword(token, userId, oldPassword, newPassword, confirmPassword,
                        onSuccess = {
                            showToast(it)
                            finish()
                        },
                        onError = {
                            showToast(it)
                        })
                }
            }
        }

//        binding.saveButton.setOnClickListener {
//            val oldPassword = binding.oldPassword.text.toString()
//            val newPassword = binding.newPassword.text.toString()
//            val confirmPassword = binding.newPassword.text.toString()
//
//            if (newPassword != confirmPassword) {
//                showToast("New password and confirm password do not match")
//                return@setOnClickListener
//            }
//
//            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
//                showToast("All fields are required")
//                return@setOnClickListener
//            }
//
//            viewModel.changePassword(token, userId, oldPassword, newPassword, confirmPassword,
//                onSuccess = {
//                    showToast(it)
//                    finish()
//                },
//                onError = {
//                    showToast(it)
//                })
//        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}