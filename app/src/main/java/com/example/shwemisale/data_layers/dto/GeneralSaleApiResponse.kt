package com.example.shwemisale.data_layers.dto

import com.example.shwemisale.data_layers.dto.printing.User

data class GeneralSaleApiResponse(
    val data:List<GeneralSaleDto>
)

data class GeneralSaleDto(
    var id:String?,
    val name:String?,
    val gold_weight_gm:String?,
    val qty:String?,
    val type:String?
)

data class GeneralSalePrintResponse(
    val data:GeneralSalePrintDto
)

data class GeneralSalePrintDto(
    val code: String?,
    val items: List<GeneralSalePrintItem>?,
    val paid_amount: Int?,
    val polo_cost: Int?,
    val reduced_cost: Int?,
    val remaining_amount: Int?,
    val salesperson: String?,
    val sold_at: String?,
    val stocks_from_home_cost: Int?,
    val total_cost: String?,
    val user: User?,
)

data class GeneralSalePrintItem(
    val cost: Int?,
    val name: String?,
    val qty: Int?
)

