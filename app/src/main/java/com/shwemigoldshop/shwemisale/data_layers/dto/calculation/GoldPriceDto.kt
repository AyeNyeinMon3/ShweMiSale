package com.shwemigoldshop.shwemisale.data_layers.dto.calculation

data class GoldPriceResponse(
    val data:GoldPriceDto
)

data class GoldPriceDto(
    val gold_type_id:String?,
    val gold_price:String?,
    val gold_price_unit:String?
)
