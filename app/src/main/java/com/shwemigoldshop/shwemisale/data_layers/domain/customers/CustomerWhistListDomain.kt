package com.shwemigoldshop.shwemisale.data_layers.domain.customers

import com.shwemigoldshop.shwemisale.data_layers.domain.product.ProductDomain
import com.shwemigoldshop.shwemisale.data_layers.ui_models.customers.CustomerWhistListUiModel

data class CustomerWhistListDomain(
    val id:String,
    val name:String,
    val image:String,
    val product:List<ProductDomain>?
)

fun CustomerWhistListDomain.asUiModel():CustomerWhistListUiModel{
    return CustomerWhistListUiModel(id,name, image, product = product.orEmpty())
}
