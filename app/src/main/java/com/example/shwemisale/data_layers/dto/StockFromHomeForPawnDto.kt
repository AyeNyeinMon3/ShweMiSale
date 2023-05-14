package com.example.shwemisale.data_layers.dto

import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.data_layers.dto.goldFromHome.Image
import com.example.shwemisale.data_layers.dto.goldFromHome.asDomain
import com.example.shwemisale.screen.goldFromHome.getGramFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY

data class StockFromHomForPawnResponse(
    val data: List<StockFromHomeForPawnDto>
)

data class StockFromHomeForPawnDto(
    val a_buying_price: String?,
    val b_voucher_buying_value: String?,
    val c_voucher_buying_price: String?,
    val calculated_buying_value: String?,
    val calculated_for_pawn: String?,
    val d_gold_weight_ywae: String?,
    val e_price_from_new_voucher: String?,
    val f_voucher_shown_gold_weight_ywae: String?,
    val gem_value: String?,
    val gem_weight_details: List<GemWeightDetail>?,
    val gem_weight_ywae: String?,
    val gold_gem_weight_ywae: String?,
    val gold_weight_ywae: String?,
    val gq_in_carat: String?,
    val has_general_expenses: String?,
    val id: String?,
    val image: Image,
    val impurities_weight_ywae: String?,
    val maintenance_cost: String?,
    val price_for_pawn: String?,
    val pt_and_clip_cost: String?,
    val qty: String?,
    val rebuy_price: String?,
    val rebuy_price_vertical_option: String,
    val size: String,
    val stock_condition: String,
    val stock_name: String,
    val type: String?,
    val wastage_ywae: String?
)
fun StockFromHomeForPawnDto.asDomain(): StockFromHomeDomain {
    return StockFromHomeDomain(
        id?.toInt()?:0,
        a_buying_price?.toDouble()?.toInt().toString(),
        b_voucher_buying_value?.toDouble()?.toInt().toString(),
        c_voucher_buying_price?.toDouble()?.toInt().toString(),
        calculated_buying_value?.toDouble()?.toInt().toString(),
        calculated_for_pawn?.toDouble()?.toInt().toString(),
        d_gold_weight_ywae?.toDouble()?.toInt().toString(),
        e_price_from_new_voucher?.toDouble()?.toInt().toString(),
        f_voucher_shown_gold_weight_ywae,
        gem_value?.toDouble()?.toInt().toString(),
        gem_weight_details?.map { it.asDomain() },
        getGramFromYwae(gold_gem_weight_ywae.let { if (it.isNullOrEmpty()) 0.0 else it.toDouble() }).toString(),
        gem_weight_ywae,
        gold_gem_weight_ywae,
        gold_weight_ywae,
        gq_in_carat,
        has_general_expenses,
        image,
        impurities_weight_ywae,
        maintenance_cost?.toDouble()?.toInt().toString(),
        price_for_pawn?.toDouble()?.toInt().toString(),
        pt_and_clip_cost?.toDouble()?.toInt().toString(),
        qty,
        rebuy_price?.toDouble()?.toInt().toString(),
        size,
        stock_condition,
        stock_name,
        type,
        wastage_ywae,
        rebuy_price_vertical_option,
        emptyList(),
        isEditable = false,
        isChecked = false,
        isFromPawn = true
    )
}