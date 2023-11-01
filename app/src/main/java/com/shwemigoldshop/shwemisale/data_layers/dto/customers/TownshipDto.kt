package com.shwemigoldshop.shwemisale.data_layers.dto.customers

data class TownshipApiResponse(
    val data:List<TownshipDto>
)

data class TownshipDto(
    val id:String?,
    val name:String?
)
