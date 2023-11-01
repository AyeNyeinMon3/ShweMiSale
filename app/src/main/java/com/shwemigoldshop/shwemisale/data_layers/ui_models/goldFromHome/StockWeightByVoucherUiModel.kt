package com.shwemigoldshop.shwemisale.data_layers.ui_models.goldFromHome

data class StockWeightByVoucherUiModel(
    val id: String,
    val code: String,
    val gold_and_gem_weight_gm: String,
    var isChecked:Boolean = false
)