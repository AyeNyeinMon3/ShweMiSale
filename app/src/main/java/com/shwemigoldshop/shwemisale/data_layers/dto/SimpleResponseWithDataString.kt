package com.shwemigoldshop.shwemisale.data_layers.dto

import com.shwemigoldshop.shwemisale.data_layers.dto.auth.ResponseDto

data class SimpleResponseWithDataString(
    val response: ResponseDto,
    val data:String?
)
data class SimpleResponse(
    val response: ResponseDto,
    )

data class SimpleError(
    val response: ResponseDto,
)