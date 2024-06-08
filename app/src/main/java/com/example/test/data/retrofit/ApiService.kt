package com.example.test.data.retrofit

import com.example.test.data.response.AddResponse
import com.example.test.data.response.DetailResponse
import com.example.test.data.response.LoginResponse
import com.example.test.data.response.RegisterResponse
import com.example.test.data.response.SharingResponse
import com.example.test.data.response.StoryResponse
import com.example.test.data.response.UpdatePasswordResponse
import com.example.test.data.response.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("signup")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("signin")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

//    @GET("stories")
//    fun getStories(
//        @Header("Authorization") token: String,
//        @Query("page") page: Int = 1,
//        @Query("size") size: Int = 10
//    ): Call<StoryResponse>

//    @GET("stories/{id}")
//    fun getDetailStories(
//        @Header("Authorization") token: String,
//        @Path("id") id: String
//    ): Call<DetailResponse>

//    @Multipart
//    @POST("stories")
//    fun uploadImage2(
//        @Header("Authorization") token: String,
//        @Part file: MultipartBody.Part,
////        @Part("description") description: RequestBody,
//    ): Call<AddResponse>

//    @GET("stories")
//    fun getStoriesWithLocation(
//        @Header("Authorization") token: String,
//        @Query("location") location : Int = 1
//    ): Call<StoryResponse>

    @PUT("users/{uid}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("uid") uid: String,
        @Body userInfo: Map<String, String>
    ): Response<UpdateProfileResponse>

    @Multipart
    @POST("sharing")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part("content") content: RequestBody,
        @Part imgUrl: MultipartBody.Part?
//        @Body requestBody: RequestBody
    ): Call<SharingResponse>

    @PUT("users/change-password/{user_id}")
    fun changePassword(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
        @Body passwordChangeRequest: PasswordChangeRequest
    ): Call<UpdatePasswordResponse>
}

data class PasswordChangeRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

//data class PostSharingRequest(
//    val content: String,
//    val imgUrl: String?
//)