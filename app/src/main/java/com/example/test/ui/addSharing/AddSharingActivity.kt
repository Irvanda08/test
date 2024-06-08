package com.example.test.ui.addSharing

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.example.test.data.response.AddResponse
import com.example.test.data.response.SharingResponse
import com.example.test.data.retrofit.ApiConfig
import com.example.test.data.util.encodeImageToBase64
import com.example.test.data.util.getImageUri
import com.example.test.data.util.reduceFileImage
import com.example.test.data.util.uriToFile
import com.example.test.databinding.ActivityAddSharingBinding
import com.example.test.ui.ViewModelFactory
import com.example.test.ui.addSharing.AddSharingViewModel
import com.example.test.ui.main.MainActivity
import com.example.test.ui.welcome.WelcomeActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class AddSharingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSharingBinding

    private val viewModel by viewModels<AddSharingViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var token = ""

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddSharingBinding.inflate(layoutInflater)
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
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token
                binding.galleryButton.setOnClickListener { startGallery() }
                binding.cameraButton.setOnClickListener { startCamera() }
                binding.uploadButton.setOnClickListener { uploadImage() }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            showImage()
        } ?: Log.d("AddSharingActivity", "No media selected")
    }

    private fun showImage() {
        currentImageUri?.let { binding.previewImageView.setImageURI(it) }
    }

    private fun startCamera() {
        val uri = currentImageUri ?: getImageUri(this)
        launcherCamera.launch(uri)
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) showImage()
    }

//    private fun uploadImage() {
//        val description = binding.etDescription.text.toString()
//
//        if (description.length < 10) {
//            showToast("Description must be at least 10 characters long")
//            return
//        }
//
//        showLoading(true)
//
//        val requestBody = description.toRequestBody("text/plain".toMediaType())
//        val imageBase64: String? = currentImageUri?.let { uri ->
//            val imageFile = uriToFile(uri, this).reduceFileImage()
//            encodeImageToBase64(imageFile)
//        } ?: "https://imageexample.com"
//
//        val jsonBody = """
//        {
//            "content": "$description",
//            "imgUrl": "${imageBase64 ?: ""}"
//        }
//    """.trimIndent().toRequestBody("application/json".toMediaType())
//
//        lifecycleScope.launch {
//            try {
//                val apiService = ApiConfig.getApiService()
//                val call = apiService.uploadImage("Bearer $token", jsonBody)
//                call.enqueue(object : Callback<SharingResponse> {
//                    override fun onResponse(call: Call<SharingResponse>, response: Response<SharingResponse>) {
//                        showLoading(false)
//                        if (response.isSuccessful) {
//                            Log.e(ContentValues.TAG, "onSuccess: ${response.message()}")
//                            backToMainActivity()
//                        } else {
//                            Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
//                            showToast(response.message())
//                        }
//                    }
//
//                    override fun onFailure(call: Call<SharingResponse>, t: Throwable) {
//                        showLoading(false)
//                        Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
//                        showToast(t.message.toString())
//                    }
//                })
//            } catch (e: HttpException) {
//                showLoading(false)
//                val errorBody = e.response()?.errorBody()?.string()
//                val errorResponse = Gson().fromJson(errorBody, SharingResponse::class.java)
//                showToast(errorResponse.message.toString())
//            }
//        }
//    }

    private fun uploadImage() {
        val description = binding.etDescription.text.toString()

        if (description.length < 10) {
            showToast("Description must be at least 10 characters long")
            return
        }

        showLoading(true)

        val requestBody = description.toRequestBody("text/plain".toMediaType())

        val imagePart: MultipartBody.Part? = currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("imgUrl", imageFile.name, requestImageFile)
        } ?: MultipartBody.Part.createFormData("imgUrl", "", "https://imageexample.com".toRequestBody("text/plain".toMediaType()))

        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val call = apiService.uploadImage("Bearer $token", requestBody, imagePart)

                call.enqueue(object : Callback<SharingResponse> {
                    override fun onResponse(call: Call<SharingResponse>, response: Response<SharingResponse>) {
                        showLoading(false)
                        if (response.isSuccessful) {
                            Log.e(ContentValues.TAG, "onSuccess: ${response.message()}")
                            backToMainActivity()
                        } else {
                            Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
                            showToast(response.message())
                        }
                    }

                    override fun onFailure(call: Call<SharingResponse>, t: Throwable) {
                        showLoading(false)
                        Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
                        showToast(t.message.toString())
                    }
                })
            } catch (e: HttpException) {
                showLoading(false)
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, SharingResponse::class.java)
                showToast(errorResponse.message.toString())
            }
        }
    }

    private fun backToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
