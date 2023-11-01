package com.shwemigoldshop.shwemisale.data_layers.dto.customers

import com.shwemigoldshop.shwemisale.data_layers.dto.auth.ResponseDto

data class AddNewCustomerResponse(
    val response: ResponseDto,
    val data:CustomerDataDto
)
