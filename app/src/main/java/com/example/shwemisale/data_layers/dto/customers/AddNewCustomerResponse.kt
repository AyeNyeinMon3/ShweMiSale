package com.example.shwemisale.data_layers.dto.customers

import com.example.shwemi.network.dto.ResponseDto

data class AddNewCustomerResponse(
    val response:ResponseDto,
    val data:CustomerDataDto
)
