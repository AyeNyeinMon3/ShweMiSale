package com.example.shwemisale.data_layers.dto.goldFromHome

import com.example.shwemisale.data_layers.ShweMiFile
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeInfoDomain

data class StockFromeHomeInfoResponse(
    val data:List<StockFromHomeInfoDto>
)

data class StockFromHomeInfoDto(
    val id: String?,
    val code: String?,
    val derived_gold_type_id: Int?,
    val derived_net_gold_weight_kpy: Double?,
    val derived_net_gold_weight_ywae: Double?,
    val gem_value: Int?,
    val gem_weight_ywae: Double?,
    val gold_and_gem_weight_gm: Double?,
    val gold_price: Int?,
    val image: ShweMiFile?,
    val maintenance_cost: Int?,
    val name: String?,
    val pt_and_clip_cost: Int?,
    val wastage_ywae: Double?
)

fun StockFromHomeInfoDto.asDomain():StockFromHomeInfoDomain{
    return StockFromHomeInfoDomain(
        id = id.orEmpty(),
        code = code.orEmpty(),
        derived_gold_type_id = derived_gold_type_id?:0,
        derived_net_gold_weight_kpy = derived_net_gold_weight_kpy?:0.0,
        derived_net_gold_weight_ywae = derived_net_gold_weight_ywae?:0.0,
        gem_value = gem_value?:0,
        gem_weight_ywae = gem_weight_ywae?:0.0,
        gold_and_gem_weight_gm = gold_and_gem_weight_gm?:0.0,
        gold_price = gold_price?:0,
        image = image?.url.orEmpty(),
        imageId = image?.id.orEmpty(),
        name = name.orEmpty(),
        maintenance_cost = maintenance_cost?:0,
        pt_and_clip_cost = pt_and_clip_cost?:0,
        wastage_ywae = wastage_ywae?:0.0,
        gem_details_qty = emptyList(),
        gem_details_gm_per_units = emptyList(),
        gem_details_ywae_per_units = emptyList()
    )
}