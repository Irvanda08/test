package com.example.test.data.response

import com.google.gson.annotations.SerializedName

data class UpdatePasswordResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
