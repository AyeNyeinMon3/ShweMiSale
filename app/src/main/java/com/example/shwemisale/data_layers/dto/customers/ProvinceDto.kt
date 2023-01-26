package com.example.shwemisale.data_layers.dto.customers

data class ProvinceApiResponse(
    val data:List<ProvinceDto>
)

data class ProvinceDto(
    val id:String?,
    val name:String?
)
