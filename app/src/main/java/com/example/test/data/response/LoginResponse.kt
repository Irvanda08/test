package com.example.test.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)