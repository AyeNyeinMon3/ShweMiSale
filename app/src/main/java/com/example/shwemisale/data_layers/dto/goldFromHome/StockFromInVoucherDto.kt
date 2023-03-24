package com.example.shwemisale.data_layers.dto.goldFromHome

import com.example.shwemisale.data_layers.ShweMiFile
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain

data class StockFromHomeInVoucherResponse(
    val data:List<StockFromInVoucherDto>
)

data class StockFromInVoucherDto(
    val id: String?,
    val code: String?,
    val derived_gold_type_id: String?,
    val derived_net_gold_weight_kpy: String?,
    val derived_net_gold_weight_ywae: String?,
    val gem_value: String?,
    val gem_weight_ywae: String?,
    val gold_and_gem_weight_gm: String?,
    val gold_price: String?,
    val image: ShweMiFile?,
    val maintenance_cost: String?,
    val name: String?,
    val pt_and_clip_cost: String?,
    val wastage_ywae: String?
)

fun StockFromInVoucherDto.asDomain():StockFromHomeDomain{
    return StockFromHomeDomain(
        a_buying_price = "0",
        b_voucher_buying_value = "0",
        c_voucher_buying_price = "0",
        calculated_buying_value = "0",
        calculated_for_pawn = "0",
        d_gold_weight_ywae = "0",
        e_price_from_new_voucher = "0",
        f_voucher_shown_gold_weight_ywae = "0",
        gem_value = gem_value.orEmpty(),
        gem_weight_ywae = gem_weight_ywae.orEmpty(),
        gem_weight_details = emptyList(),
        gold_gem_weight_ywae = gold_and_gem_weight_gm,
        gold_weight_ywae = derived_net_gold_weight_ywae,
        gq_in_carat = "0"    ,
        has_general_expenses = "0",
        image = image?.asImage(),
        impurities_weight_ywae = "0" ,
        maintenance_cost = maintenance_cost,
        price_for_pawn = "0",
        pt_and_clip_cost = pt_and_clip_cost,
        qty = "0",
        rebuy_price = "0",
        size = "small",
        stock_condition = "damage",
        stock_name = name,
        type = "1",
        wastage_ywae = wastage_ywae,
        rebuy_price_vertical_option = "X"
    )
}

