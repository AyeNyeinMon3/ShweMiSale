package com.example.shwemisale.data_layers.domain.goldFromHome

import com.example.shwemisale.data_layers.ShweMiFile
import com.example.shwemisale.data_layers.dto.goldFromHome.StockFromHomeInfoDto
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae

data class StockFromHomeInfoDomain(
    val id: String,
    val code: String,
    val derived_gold_type_id: Int,
    val derived_net_gold_weight_kpy: Double,
    val derived_net_gold_weight_ywae: Double,
    val gem_value: Int,
    val gem_weight_ywae: Double,
    val gold_and_gem_weight_gm: Double,
    val gold_price: Int,
    val image: String?,
    val imageId: String?,
    val maintenance_cost: Int,
    val name: String,
    val pt_and_clip_cost: Int,
    val wastage_ywae: Double
)

//fun StockFromHomeInfoDomain.asUiModel():StockFromHomeInfoUiModel{
//    val derivedNetGoldWeightKPY = getKPYFromYwae(derived_net_gold_weight_ywae)
//    val derivedNetGoldWeightKyat = derivedNetGoldWeightKPY[0]
//    val derivedNetGoldWeightPae = derivedNetGoldWeightKPY[1]
//    val derivedNetGoldWeightYwae = derivedNetGoldWeightKPY[2]
//
//    val gemWeightKPY = getKPYFromYwae(gem_weight_ywae)
//    val gemWeightKyat = gemWeightKPY[0]
//    val gemWeightPae = gemWeightKPY[1]
//    val gemWeightYwae = gemWeightKPY[2]
//
//    val wastageKPY = getKPYFromYwae(wastage_ywae)
//    val wastageKyat = wastageKPY[0]
//    val wastagePae = wastageKPY[1]
//    val wastageYwae = wastageKPY[2]
//
//    return StockFromHomeInfoUiModel(
//        id = id,
//        code = code,
//        derived_gold_type_id = derived_gold_type_id.toString(),
//        derived_net_gold_weight_kpy = derived_net_gold_weight_kpy.toString(),
//        derived_net_gold_weight_kyat = derivedNetGoldWeightKyat.toInt().toString(),
//        derived_net_gold_weight_pae = derivedNetGoldWeightPae.toInt().toString(),
//        derived_net_gold_weight_ywae = derivedNetGoldWeightYwae.toString(),
//        gem_value = gem_value.toString(),
//        gem_weight_kyat = gemWeightKyat.toInt().toString(),
//        gem_weight_pae = gemWeightPae.toInt().toString(),
//        gem_weight_ywae = gemWeightYwae.toInt().toString(),
//        gold_and_gem_weight_gm = gold_and_gem_weight_gm.toString(),
//        gold_price = gold_price.toString(),
//        image = image,
//        maintenance_cost = maintenance_cost.toString(),
//        name=name,
//        pt_and_clip_cost = pt_and_clip_cost.toString(),
//        wastage_kyat = wastageKyat.toInt().toString(),
//        wastage_pae = wastagePae.toInt().toString(),
//        wastage_ywae = wastageYwae.toInt().toString()
//    )
//}