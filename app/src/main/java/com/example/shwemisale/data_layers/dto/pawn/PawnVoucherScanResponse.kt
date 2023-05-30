package com.example.shwemisale.data_layers.dto.pawn

import com.example.shwemisale.data_layers.domain.PawnVoucherScanDomain

data class PawnVoucherScanResponse(
    val data: PawnVoucherScanDto
)

data class PawnVoucherScanDto(
    val interest_amount: String?,
    val interest_days: String?,
    val interest_per_month: String?,
    val prepaid_debt: String?,
    val prepaid_months: String?,
    val remaining_debt: String?,
    val total_debt_amount: String?,
    val transaction_id: String?,
    val username: String?,
    val tier_discount: String?,
    val tier_discount_percentage: String?,
    val tier_name: String?,
    val remark: String?
)

fun PawnVoucherScanDto.asDomain(): PawnVoucherScanDomain {
    return PawnVoucherScanDomain(
        interest_amount.orEmpty(),
        interest_days.orEmpty(),
        interest_per_month.orEmpty(),
        prepaid_debt.orEmpty(),
        prepaid_months.orEmpty(),
        remaining_debt.orEmpty(),
        total_debt_amount.orEmpty(),
        transaction_id.orEmpty(),
        username.orEmpty(),
        tier_discount.orEmpty(),
        tier_discount_percentage.orEmpty(),
        tier_name.orEmpty(),
        remark.orEmpty()
    )
}