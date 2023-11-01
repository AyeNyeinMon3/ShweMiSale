package com.shwemigoldshop.shwemisale.data_layers.dto.calculation

data class GoldTypePriceApiResponse(
    val data:List<GoldTypePriceDto>
)

data class GoldTypePriceDto(
    val id:String?,
    val name:String?,
    val public_name:String?,
    val price:String?,
    val per_unit:String?

)
