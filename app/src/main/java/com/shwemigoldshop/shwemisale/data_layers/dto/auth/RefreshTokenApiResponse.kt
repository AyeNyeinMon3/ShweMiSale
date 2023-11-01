package com.shwemigoldshop.shwemisale.data_layers.dto.auth

import com.squareup.moshi.Json

data class RefreshTokenApiResponse(
    val response: ResponseDto?,
    val data: RefreshTokenDataDto?
)

data class RefreshTokenDataDto(
    val token: String?,
    @field:Json(name = "token_type")
    val tokenType: String?,
    @field:Json(name = "access_token_expires_in")
    val accessTokenExpiresIn: Long?,
    @field:Json(name = "refresh_token_expires_in")
    val refresh_token_expires_in: Long?
)

data class RefreshTokenErrorResponse(
    val message: String?,
    val success: Boolean?
)
