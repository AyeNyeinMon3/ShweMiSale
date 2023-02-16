package com.example.shwemisale.data_layers.dto.goldFromHome

import com.example.shwemisale.data_layers.domain.goldFromHome.StockWeightByVoucherDomain

data class StockWeightByVoucherResponse(
    val data:List<StockWeightByVoucherDto>
)
data class StockWeightByVoucherDto(
    val id:String?,
    val code:String?,
    val gold_and_gem_weight_gm:String?
)

fun StockWeightByVoucherDto.asDomain():StockWeightByVoucherDomain{
    return StockWeightByVoucherDomain(
        id = id.orEmpty(),
        code = code.orEmpty(),
        gold_and_gem_weight_gm = gold_and_gem_weight_gm.orEmpty()
    )
}
