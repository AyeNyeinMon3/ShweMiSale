package com.example.shwemi.network.dto.auth

import com.example.shwemi.network.dto.ResponseDto

data class ProfileApiResponse(
    val response: ResponseDto?,
    val data: ProfileDto?
)

data class ProfileDto(
    val name:String?
)
