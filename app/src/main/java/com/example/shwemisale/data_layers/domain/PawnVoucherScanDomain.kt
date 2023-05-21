package com.example.shwemisale.data_layers.domain

data class PawnVoucherScanDomain(
    val interest_amount: String,
    val interest_days: String,
    val interest_per_month: String,
    val prepaid_debt: String,
    val prepaid_months: String,
    val remaining_debt: String,
    val total_debt_amount: String,
    val transaction_id: String,
    val username: String,
    val tier_discount: String,
    val tier_name: String,
    val remark:String
)
