package com.example.shwemisale.data_layers.dto

import com.example.shwemi.network.dto.ResponseDto

data class CreatePawnResponse(
    val response: ResponseDto,
    val data:CreatePawnDto?
)
data class CreatePawnDto(
    val id:String?,
)