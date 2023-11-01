package com.shwemigoldshop.shwemisale.data_layers.dto.printing

data class PawnCreatePrintResponse(
    val data:PawnCreatePrintDto
)
data class PawnCreatePrintDto(
    val code: String,
    val invoiced_date: String?,
    val pawned_stocks: List<PawnedStock>,
    val remaining_debt: String,
    val interest_amount:String,
    val reduced_amount:String,
    val salesperson: String,
    val status: String,
    val user: User,
    val warning_period_months: Int
)

data class PawnedStock(
    val d_gold_weight_ywae: String?,
    val qty: Int,
    val stock_name: String,
    val c_voucher_buying_price:String?,
    val b_voucher_buying_value:String?,
)

