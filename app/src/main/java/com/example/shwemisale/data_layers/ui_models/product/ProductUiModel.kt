package com.example.shwemisale.data_layers.ui_models.product

import com.example.shwemisale.data_layers.domain.ShweMiFileDomain

data class ProductUiModel(
    val id:String,
    val code:String,
    val name:String,
    val cost:String,
    val size:String,
    val thumbnail: ShweMiFileDomain?
)
