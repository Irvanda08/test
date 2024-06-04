package com.example.test.ui.main

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.UserRepository
import com.example.test.data.pref.UserModel
import com.example.test.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: UserRepository) : ViewModel() {
//    private val _story = MutableLiveData<List<ListStoryItem>>()
//    val story: LiveData<List<ListStoryItem>> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

//    fun getStory(token: String) {
//        _isLoading.value = true
//        val client = ApiConfig.getApiService().getStories("Bearer $token")
//        client.enqueue(object : Callback<StoryResponse> {
//            override fun onResponse(
//                call: Call<StoryResponse>,
//                response: Response<StoryResponse>
//            ) {
//                _isLoading.value = false
//                if (response.isSuccessful) {
//                    _story.value = response.body()?.listStory
//                } else {
//                    Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
//                _isLoading.value = false
//                Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
//            }
//        })
//    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}