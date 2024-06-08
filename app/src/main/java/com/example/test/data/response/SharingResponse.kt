package com.example.test.data.response

import com.google.gson.annotations.SerializedName

data class SharingResponse(

	@field:SerializedName("dataPost")
	val dataPost: DataPost? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataPost(

	@field:SerializedName("sharing_id")
	val sharingId: String? = null,

	@field:SerializedName("imgUrl")
	val imgUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)
