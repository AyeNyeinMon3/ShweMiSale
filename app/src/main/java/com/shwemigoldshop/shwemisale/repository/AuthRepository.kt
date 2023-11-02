package com.shwemigoldshop.shwemisale.repository

import com.shwemigoldshop.shwemisale.util.Resource


interface AuthRepository {
    fun getNewAccessToken(): Resource<String>
    fun refreshTokenLog(): Resource<String>

    suspend fun login(userName:String,password:String): Resource<String>

    suspend fun logout(): Resource<String>

    suspend fun getProfile(): Resource<String>
    suspend fun authorizeApp(): Resource<String>
}