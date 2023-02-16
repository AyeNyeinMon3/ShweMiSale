package com.example.shwemisale.data_layers.domain.goldFromHome

import com.example.shwemisale.data_layers.dto.goldFromHome.StockWeightByVoucherDto
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel

data class StockWeightByVoucherDomain(
    val id:String,
    val code:String,
    val gold_and_gem_weight_gm:String
)

fun StockWeightByVoucherDomain.asUiModel():StockWeightByVoucherUiModel{
    return StockWeightByVoucherUiModel(
        id = id.orEmpty(),
        code = code.orEmpty(),
        gold_and_gem_weight_gm = gold_and_gem_weight_gm.orEmpty()
    )
}
