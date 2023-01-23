package com.example.shwemisale.network.api_services

import com.example.shwemi.network.dto.auth.LoginApiResponse
import com.example.shwemi.network.dto.auth.ProfileApiResponse
import com.example.shwemi.network.dto.auth.RefreshTokenApiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @GET("api/auth/refresh")
    fun refreshToken(
        @Header("Authorization") token: String
    ): Call<RefreshTokenApiResponse>

    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginApiResponse>

    @GET("api/profile")
    suspend fun getProfile(
        @Header("Authorization") token:String
    ): Response<ProfileApiResponse>
}