package com.example.shwemisale.data_layers.dto

import com.example.shwemi.network.dto.ResponseDto

data class SimpleResponse(
    val response:ResponseDto,
    val data:String?
)
data class SimpleError(
    val response: ResponseDto,
)