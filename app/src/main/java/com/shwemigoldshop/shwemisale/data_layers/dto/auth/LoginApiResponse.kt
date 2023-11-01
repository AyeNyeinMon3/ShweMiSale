package com.shwemigoldshop.shwemisale.data_layers.dto.auth


data class LoginApiResponse(
    val response: ResponseDto?,
    val data: AuthDataDto?
)

data class AuthDataDto(
    val token:String?
)
