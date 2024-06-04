package com.example.test.ui.detail

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.test.data.UserRepository
import com.example.test.data.pref.UserModel
import com.example.test.data.response.DetailResponse
import com.example.test.data.response.Story
import com.example.test.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    private val mstory = MutableLiveData<Story>()
    val story: LiveData<Story> = mstory

    private val misLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = misLoading

    fun getDetailStory(token: String, id: String) {
        misLoading.value = true
        val client = ApiConfig.getApiService().getDetailStories("Bearer $token", id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                misLoading.value = false
                if (response.isSuccessful) {
                    mstory.value = response.body()?.story as Story
                } else {
                    Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                misLoading.value = false
                Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
            }
        })
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}