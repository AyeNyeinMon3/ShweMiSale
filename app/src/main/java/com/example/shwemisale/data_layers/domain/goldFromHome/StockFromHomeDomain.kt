package com.example.shwemisale.data_layers.domain.goldFromHome

import android.os.Parcelable
import com.example.shwemisale.data_layers.ShweMiFile
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.data_layers.dto.goldFromHome.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StockFromHomeDomain(
    var id :Int?,
    val a_buying_price: String?,
    val b_voucher_buying_value: String?,
    val c_voucher_buying_price: String?,
    val calculated_buying_value: String?,
    val calculated_for_pawn: String?,
    val d_gold_weight_ywae: String?,
    val e_price_from_new_voucher: String?,
    val f_voucher_shown_gold_weight_ywae: String?,
    val gem_value: String?,
    val gem_weight_details_session_key:String?,
    var gold_and_gem_weight_gm:String = "",
    val gem_weight_ywae: String?,
    val gold_gem_weight_ywae: String?,
    val gold_weight_ywae: String?,
    val gq_in_carat: String?,
    val has_general_expenses: String?,
    val image: Image?,
    val impurities_weight_ywae: String?,
    val maintenance_cost: String?,
    val price_for_pawn: String?,
    val pt_and_clip_cost: String?,
    val qty: String?,
    val rebuy_price: String,
    val size: String?,
    val stock_condition: String?,
    val stock_name: String?,
    val type: String?,
    val wastage_ywae: String?,
    val rebuy_price_vertical_option:String?,
    val productId:List<String>?,
    var isEditable:Boolean,
    var isChecked:Boolean,
    var localId:Int = 0,
    var derived_gold_type_id:String = ""
):Parcelable
