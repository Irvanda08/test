package com.example.test.ui.addList

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.example.test.data.response.AddResponse
import com.example.test.data.retrofit.ApiConfig
import com.example.test.data.util.getImageUri
import com.example.test.data.util.reduceFileImage
import com.example.test.data.util.uriToFile
import com.example.test.databinding.ActivityAddListBinding
import com.example.test.ui.ViewModelFactory
import com.example.test.ui.addList.AddListViewModel
import com.example.test.ui.main.MainActivity
import com.example.test.ui.welcome.WelcomeActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class AddListActivity : AppCompatActivity(){
    private lateinit var binding: ActivityAddListBinding
    private val viewModel by viewModels<AddListViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var token = "token"

    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Add Story"

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token
                binding.btGallery.setOnClickListener { startGallery() }
                binding.btCamera.setOnClickListener { startCamera() }
                binding.btUpload.setOnClickListener {
                    uploadImage()
                }
            }
        }
    }

    private fun backToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.etDescription.text.toString()
            showLoading(true)

//            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val successResponse = apiService.uploadImage("Bearer $token", multipartBody)
                    Log.d(ContentValues.TAG, "uploadImage token: $token")
                    Log.d(ContentValues.TAG, "uploadImage: berhasil")
                    showToast(getString(R.string.success_upload))

                    successResponse.enqueue(object : Callback<AddResponse> {
                        override fun onResponse(
                            call: Call<AddResponse>,
                            response: Response<AddResponse>
                        ) {

                            if (response.isSuccessful) {
                                Log.e(ContentValues.TAG, "onSuccess: ${response.message()}")
                                backToMainActivity()
                            } else {
                                Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<AddResponse>, t: Throwable) {

                            Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
                        }
                    })
                    showLoading(false)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, AddResponse::class.java)
                    showToast(errorResponse.message.toString())
                    showLoading(false)
                }
            }
        } ?: showToast(getString(R.string.empty_image))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.lpiProgressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}