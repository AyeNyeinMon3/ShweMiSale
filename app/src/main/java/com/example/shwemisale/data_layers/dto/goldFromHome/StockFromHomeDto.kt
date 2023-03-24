package com.example.shwemisale.data_layers.dto.goldFromHome

import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain

data class StockFromHomeDto(
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
    val image: Image,
    val impurities_weight_ywae: String?,
    val maintenance_cost: String?,
    val price_for_pawn: String?,
    val pt_and_clip_cost: String?,
    val qty: String?,
    val rebuy_price: String?,
    val size: String?,
    val stock_condition: String?,
    val stock_name: String?,
    val type: String?,
    val wastage_ywae: String?,
    val rebuy_price_vertical_option:String?

)

fun StockFromHomeDto.asDomain(): StockFromHomeDomain {
    return StockFromHomeDomain(
        0,
        a_buying_price,
        b_voucher_buying_value,
        c_voucher_buying_price,
        calculated_buying_value,
        calculated_for_pawn,
        d_gold_weight_ywae,
        e_price_from_new_voucher,
        f_voucher_shown_gold_weight_ywae,
        gem_value,
        gem_weight_details,
        gem_weight_ywae,
        gold_gem_weight_ywae,
        gold_weight_ywae,
        gq_in_carat,
        has_general_expenses,
        image,
        impurities_weight_ywae,
        maintenance_cost,
        price_for_pawn,
        pt_and_clip_cost,
        qty,
        rebuy_price,
        size,
        stock_condition,
        stock_name,
        type,
        wastage_ywae,
        rebuy_price_vertical_option
    )
}

data class StockFromHomeResponse(
    val data:List<StockFromHomeDto>
)