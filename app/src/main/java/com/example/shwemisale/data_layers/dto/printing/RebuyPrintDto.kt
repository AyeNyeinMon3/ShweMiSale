package com.example.shwemisale.data_layers.dto.printing

data class RebuyPrintResponse(
    val data:RebuyPrintDto
)

data class RebuyPrintDto(
    val code: String,
    val items: List<RebuyPrintItem>,
    val salesperson: String,
    val sold_at: String,
    val total_cost: Int,
    val user: User
)

data class RebuyPrintItem(
    val name: String?,
    val gold_weight_ywae: String,
    val b_voucher_buying_value: String,
    val rebuy_price: String
)

data class User(
    val address: String?,
    val name: String,
    val phone: String,
    val province: String
)

//fun PawnedStock.asPrintData():RebuyPrintItem{
//    return RebuyPrintItem(
//        name = stock_name,
//        gold_weight_ywae = d_gold_weight_ywae.toString(),
//        b_voucher_buying_value =
//    )
//}