package com.example.shwemisale.data_layers.ui_models.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductInfoUiModel(
    val id: String,
    val code: String,
    val name: String,
    val jewellery_type_id: String,
    val size: String,
    val gold_and_gem_weight_gm: String,
    val old_gold_and_gem_weight_gm: String,
    val gem_weight_ywae: String,
    val gem_value: String,
    val promotion_discount: String,
    var pt_and_clip_cost: String,
    var maintenance_cost: String,
    val gold_weight_ywae: String,
    var wastage_weight_ywae: String,
    var cost: String,
    var gold_type_id: String,
    var edit_reason_id: String,
    var general_sale_item_id: String,
    var new_clip_wt_gm: String,
    var old_clip_wt_gm: String,
    var is_order_sale: String?,
    var order_sale_gold_price: String?,
    var order_sale_code: String?,
    val edited_gold_price:String?,
    val image: String
) : Parcelable

