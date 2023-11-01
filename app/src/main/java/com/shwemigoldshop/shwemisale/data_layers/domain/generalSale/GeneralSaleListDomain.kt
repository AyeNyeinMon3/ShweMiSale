package com.shwemigoldshop.shwemisale.data_layers.domain.generalSale

data class GeneralSaleListDomain(
    var id:Int = 0,
    val general_sale_item_id: String,
    val gold_weight_gm: String,
    val maintenance_cost: String,
    val qty: String,
    val wastage_ywae: String
)

