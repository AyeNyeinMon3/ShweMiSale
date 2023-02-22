package com.example.shwemisale.data_layers.dto

data class GeneralSaleApiResponse(
    val data:List<GeneralSaleDto>
)

data class GeneralSaleDto(
    val id:String?,
    val name:String?,
    val gold_weight_gm:String?,
    val qty:String?,
    val type:String?
)