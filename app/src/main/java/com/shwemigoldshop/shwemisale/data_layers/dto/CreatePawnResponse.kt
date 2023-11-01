package com.shwemigoldshop.shwemisale.data_layers.dto

import com.shwemigoldshop.shwemisale.data_layers.dto.auth.ResponseDto

data class CreatePawnResponse(
    val response: ResponseDto,
    val data:CreatePawnDto?
)
data class CreatePawnDto(
    val id:String?,
)