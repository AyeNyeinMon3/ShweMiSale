package com.example.shwemisale.data_layers.domain.product

import com.example.shwemisale.data_layers.ui_models.product.ProductInfoUiModel
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram

data class ProductInfoDomain(
    val id: String,
    val code: String,
    val name: String,
    val jewellery_type_id: String,
    val size: String,
    val bonus:String,
    val gold_and_gem_weight_gm: String,
    val old_gold_and_gem_weight_gm: String,
    val gem_weight_ywae: String,
    val gem_value: String,
    val promotion_discount: String,
    val pt_and_clip_cost: String,
    val maintenance_cost: String,
    val wastage_ywae: String,
    val cost: String,
    val gold_type_id: String,
    var edit_reason_id: String,
    var general_sale_item_id: String,
    var new_clip_wt_gm: String,
    var old_clip_wt_gm: String,
    var is_order_sale: String?,
    var order_sale_gold_price: String?,
    var order_sale_code: String?,
    val image: String
)

fun ProductInfoDomain.asUiModel(): ProductInfoUiModel {
    return ProductInfoUiModel(
        id,
        code,
        name,
        jewellery_type_id,
        size,
        gold_and_gem_weight_gm,
        old_gold_and_gem_weight_gm,
        gem_weight_ywae,
        gem_value,
        promotion_discount,
        pt_and_clip_cost,
        maintenance_cost,
        (getYwaeFromGram(gold_and_gem_weight_gm.toDouble()) - gem_weight_ywae.toDouble()).toString(),
        wastage_ywae,
        cost,
        gold_type_id,
        edit_reason_id = edit_reason_id.orEmpty(),
        general_sale_item_id = general_sale_item_id.orEmpty(),
        new_clip_wt_gm = new_clip_wt_gm.orEmpty(),
        old_clip_wt_gm = old_clip_wt_gm.orEmpty(),
        is_order_sale,
        order_sale_gold_price,
        order_sale_code,
        image
    )
}
