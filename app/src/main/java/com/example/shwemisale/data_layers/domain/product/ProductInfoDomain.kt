package com.example.shwemisale.data_layers.domain.product

import com.example.shwemisale.data_layers.ui_models.product.ProductInfoUiModel
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram

data class ProductInfoDomain(
    val id: String,
    val code: String,
    val name: String,
    val jewellery_type_id: String,
    val size: String,
    val gold_and_gem_weight_gm: String,
    val gem_weight_ywae: String,
    val gem_value: String,
    val promotion_discount: String,
    val pt_and_clip_cost: String,
    val maintenance_cost: String,
    val wastage_ywae: String,
    val cost: String,
    val gold_type_id: String,
    val image:String
)

fun ProductInfoDomain.asUiModel(): ProductInfoUiModel {
    return ProductInfoUiModel(
        id,
        code,
        name,
        jewellery_type_id,
        size,
        gold_and_gem_weight_gm,
        gem_weight_ywae,
        gem_value,
        promotion_discount,
        pt_and_clip_cost,
        maintenance_cost,
        (getYwaeFromGram(gold_and_gem_weight_gm.toDouble()) - gem_weight_ywae.toDouble()).toString(),
        wastage_ywae,
        cost,
        gold_type_id,
        image
    )
}
