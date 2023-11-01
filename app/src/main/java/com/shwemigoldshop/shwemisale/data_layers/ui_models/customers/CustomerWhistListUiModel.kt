package com.shwemigoldshop.shwemisale.data_layers.ui_models.customers

import com.shwemigoldshop.shwemisale.data_layers.domain.product.ProductDomain

data class CustomerWhistListUiModel(
    val id:String,
    val name:String,
    val image:String,
    val product:List<ProductDomain>
)
