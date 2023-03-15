package com.example.shwemisale.data_layers.dto.product

import com.example.shwemisale.data_layers.domain.pureGoldSale.PureGoldListDomain

data class PureGoldListResponse(
   val data:List<PureGoldListDto>
)

data class PureGoldListDto(
    val gold_weight_ywae: String?,
    val maintenance_cost: String?,
    val threading_fees: String?,
    val type: String?,
    val wastage_ywae: String?
)

fun PureGoldListDto.asDomain():PureGoldListDomain{
    return PureGoldListDomain(
        "0",gold_weight_ywae, maintenance_cost, threading_fees, type, wastage_ywae
    )
}