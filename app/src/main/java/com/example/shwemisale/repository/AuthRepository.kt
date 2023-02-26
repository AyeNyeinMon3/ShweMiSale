package com.example.shwemisale.repository

import com.example.shwemi.util.Resource


interface AuthRepository {
    fun getNewAccessToken(): Resource<String>
    fun refreshTokenLog(): Resource<String>

    suspend fun login(userName:String,password:String):Resource<String>

    suspend fun logout():Resource<String>

    suspend fun getProfile():Resource<String>
}