package com.example.shwemi.network.dto.auth

import com.example.shwemi.network.dto.ResponseDto

data class LoginApiResponse(
    val response: ResponseDto?,
    val data: AuthDataDto?
)

data class AuthDataDto(
    val token:String?
)
