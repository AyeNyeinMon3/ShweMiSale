package com.shwemigoldshop.shwemisale.data_layers.dto.auth

data class ProfileApiResponse(
    val response: ResponseDto?,
    val data: ProfileDto?
)

data class ProfileDto(
    val name:String?
)
