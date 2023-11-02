package com.shwemigoldshop.shwemisale.network.api_services

import com.shwemigoldshop.shwemisale.data_layers.dto.SimpleResponse
import com.shwemigoldshop.shwemisale.data_layers.dto.auth.LoginApiResponse
import com.shwemigoldshop.shwemisale.data_layers.dto.auth.ProfileApiResponse
import com.shwemigoldshop.shwemisale.data_layers.dto.auth.RefreshTokenApiResponse
import com.shwemigoldshop.shwemisale.data_layers.dto.auth.LogOutApiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @GET("api/auth/refresh")
    fun refreshToken(
        @Header("Authorization") token: String
    ): Call<RefreshTokenApiResponse>

    @GET("api/auth/refreshed")
    fun refreshTokenLog(
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

    @FormUrlEncoded
    @POST("api/app/authorize")
    suspend fun authorizeApp(
        @Field("authorization_code") code:String
    ):Response<SimpleResponse>

    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token:String
    ): Response<LogOutApiResponse>
}