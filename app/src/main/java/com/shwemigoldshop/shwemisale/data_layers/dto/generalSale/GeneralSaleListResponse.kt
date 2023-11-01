package com.shwemigoldshop.shwemisale.data_layers.dto.generalSale

import com.shwemigoldshop.shwemisale.data_layers.domain.generalSale.GeneralSaleListDomain

data class GeneralSaleListResponse(
    val data:List<GeneralSaleListDto>
)
data class GeneralSaleListDto(
    val general_sale_item_id: String,
    val gold_weight_gm: String,
    val maintenance_cost: String,
    val qty: String,
    val wastage_ywae: String
)

fun GeneralSaleListDto.asDomain():GeneralSaleListDomain{
    return GeneralSaleListDomain(
        0,general_sale_item_id, gold_weight_gm, maintenance_cost, qty, wastage_ywae
    )
}
