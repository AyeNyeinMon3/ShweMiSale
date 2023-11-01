package com.shwemigoldshop.shwemisale.data_layers.domain.product

import com.shwemigoldshop.shwemisale.data_layers.domain.ShweMiFileDomain
import com.shwemigoldshop.shwemisale.data_layers.ui_models.product.ProductUiModel

data class ProductDomain(
    val id:String,
    val code:String,
    val name:String,
    val cost:String,
    val size:String,
    val thumbnail: ShweMiFileDomain?
)

fun ProductDomain.asUiModel():ProductUiModel{
    return ProductUiModel(
        id, code, name, cost, size, thumbnail
    )
}
