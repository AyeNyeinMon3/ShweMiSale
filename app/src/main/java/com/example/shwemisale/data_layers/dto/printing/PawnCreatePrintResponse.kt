package com.example.shwemisale.data_layers.dto.printing

data class PawnCreatePrintResponse(
    val data:PawnCreatePrintDto
)
data class PawnCreatePrintDto(
    val code: String,
    val interest_rate: Int?,
    val invoiced_date: String?,
    val is_app_functions_allowed: Int,
    val pawned_stocks: List<PawnedStock>,
    val prepaid_debts: List<Int?>,
    val prepaid_interests: List<Int?>,
    val remaining_debt: String,
    val salesperson: String,
    val status: String,
    val user: User,
    val warning_period_months: Int
)

data class PawnedStock(
    val d_gold_weight_ywae: Int?,
    val pawned: Any,
    val qty: Int,
    val stock_name: String
)

