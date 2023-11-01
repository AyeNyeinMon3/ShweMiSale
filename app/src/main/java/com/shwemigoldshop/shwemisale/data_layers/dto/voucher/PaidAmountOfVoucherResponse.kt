package com.shwemigoldshop.shwemisale.data_layers.dto.voucher

import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.PaidAmountOfVoucherDomain

data class PaidAmountOfVoucherResponse(
    val data:PaidAmountOfVoucherDto
)
data class PaidAmountOfVoucherDto(
    val paid_amount:String,
    val old_stock_session_key:String?
)
fun PaidAmountOfVoucherDto.asDomain():PaidAmountOfVoucherDomain{
    return PaidAmountOfVoucherDomain(
        paid_amount, old_stock_session_key
    )
}