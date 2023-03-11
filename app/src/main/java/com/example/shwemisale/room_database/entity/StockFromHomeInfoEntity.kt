package com.example.shwemisale.room_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shwemisale.data_layers.ShweMiFile
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeInfoDomain
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram

@Entity(tableName = "stock-from-home")
data class StockFromHomeInfoEntity(
    @PrimaryKey val id: String,
    val code: String?,
    val derived_gold_type_id: String?,

    val derived_net_gold_weight_kpy: String?,

    val derived_net_gold_weight_ywae: String?,

    val gem_value: String?,
    val gem_details_qty: List<String>?,
    val gem_details_gm_per_units: List<String>?,
    val gem_details_ywae_per_units: List<String>?,


    val gem_weight_ywae: String?,
    val goldWeightYwae: String?,

    val gold_and_gem_weight_gm: String?,

    val gold_price: String?,
    val image: String?,
    val imageId: String?,
    val maintenance_cost: String?,
    val name: String?,
    val qty: String?,
    val size: String?,
    val pt_and_clip_cost: String?,
    val reduced_cost: String?,


    val wastage_ywae: String?,

    val rebuyPrice: String?,
    val priceForPawn: String?,//rebuy - pawn diff
    val calculatedPriceForPawn: String?,//PawnPrice =  (GoldWeight(Ywae)+Htae Pay ayawt(Ywae)->(Kyat))*ThetMhatPawnPrice + otherCostFromUI(only one for gem Discount)

    val oldStockCondition: String?,
    val oldStockGQinCarat: String?,
    val oldStockImpurityWeightY: String?,


    val oldStockABuyingPrice: String?,
    val oldStockb_voucher_buying_value: String?,
    val oldStockc_voucher_buying_value: String?,

    val oldStockDGoldWeightY: String?,
    val oldStockEPriceFromNewVoucher: String?,
    val oldStockFVoucherShownGoldWeightY: String?,


    )

fun StockFromHomeInfoEntity.asUiModel(): StockFromHomeInfoUiModel {
    return StockFromHomeInfoUiModel(
        id.toString(),
        code,
        qty,
        size,
        derived_gold_type_id,
        derived_net_gold_weight_kpy,
        derived_net_gold_weight_ywae,
        gem_value,
        gem_details_qty = gem_details_qty.orEmpty(),
        gem_details_gm_per_units = gem_details_gm_per_units.orEmpty(),
        gem_details_ywae_per_units = gem_details_ywae_per_units.orEmpty(),
        gem_weight_ywae,
        goldWeightYwae,
        gold_and_gem_weight_gm,
        gold_price,
        image,
        imageId,
        maintenance_cost,
        name,
        pt_and_clip_cost,
        reduced_cost,
        wastage_ywae,
        rebuyPrice,
        priceForPawn,
        calculatedPriceForPawn,
        oldStockCondition,
        oldStockGQinCarat,
        oldStockImpurityWeightY,
        oldStockABuyingPrice,
        oldStockb_voucher_buying_value,
        oldStockc_voucher_buying_value,
        oldStockDGoldWeightY,
        oldStockEPriceFromNewVoucher,
        oldStockFVoucherShownGoldWeightY,
    )
}

fun StockFromHomeInfoDomain.asEntity(): StockFromHomeInfoEntity {
    return StockFromHomeInfoEntity(
        id = id,
        code = code,
        derived_gold_type_id = derived_gold_type_id.toString(),
        derived_net_gold_weight_kpy = derived_net_gold_weight_kpy.toString(),
        derived_net_gold_weight_ywae = derived_net_gold_weight_ywae.toString(),
        gem_value = gem_value.toString(),
        gem_weight_ywae = gem_weight_ywae.toString(),
        gem_details_qty = gem_details_qty as ArrayList<String>,
        gem_details_gm_per_units = gem_details_gm_per_units as ArrayList<String>,
        gem_details_ywae_per_units = gem_details_ywae_per_units as ArrayList<String>,
        goldWeightYwae =
        (getYwaeFromGram(gold_and_gem_weight_gm.toDouble()) - gem_weight_ywae).toString(),
        gold_and_gem_weight_gm = gold_and_gem_weight_gm.toString(),
        gold_price = gold_price.toString(),
        image = image,
        imageId = imageId,
        maintenance_cost = maintenance_cost.toString(),
        name = name,
        qty = "0",
        size = "small",
        pt_and_clip_cost = pt_and_clip_cost.toString(),
        reduced_cost = "",
        wastage_ywae = wastage_ywae.toString(),
        rebuyPrice = "",
        priceForPawn = "",
        calculatedPriceForPawn = "",

        oldStockCondition = "",
        oldStockGQinCarat = "",
        oldStockImpurityWeightY = "",

        oldStockABuyingPrice = "",
        oldStockb_voucher_buying_value = "",
        oldStockc_voucher_buying_value = "",
        oldStockDGoldWeightY = "",
        oldStockEPriceFromNewVoucher = "",
        oldStockFVoucherShownGoldWeightY = "",
    )
}

fun StockFromHomeInfoUiModel.asEntity(): StockFromHomeInfoEntity {
    return StockFromHomeInfoEntity(
        id = id,
        code = code,
        derived_gold_type_id = derived_gold_type_id.toString(),
        derived_net_gold_weight_kpy = derived_net_gold_weight_kpy.toString(),
        derived_net_gold_weight_ywae = derived_net_gold_weight_ywae.toString(),
        gem_value = gem_value.toString(),
        gem_weight_ywae = gem_weight_ywae.toString(),
        gem_details_qty = gem_details_qty as ArrayList<String>,
        gem_details_gm_per_units = gem_details_gm_per_units as ArrayList<String>,
        gem_details_ywae_per_units = gem_details_ywae_per_units as ArrayList<String>,
        goldWeightYwae =
        (getYwaeFromGram(gold_and_gem_weight_gm.let {
            if (it.orEmpty().isEmpty()) 0.0 else it!!.toDouble()
        }) - gem_weight_ywae.let {
            if (it.orEmpty().isEmpty()) 0.0 else it!!.toDouble()
        }).toString(),
        gold_and_gem_weight_gm = gold_and_gem_weight_gm.toString(),
        gold_price = gold_price.toString(),
        image = image,
        imageId = imageId,
        maintenance_cost = maintenance_cost.toString(),
        name = name,
        qty = "0",
        size = "small",
        pt_and_clip_cost = pt_and_clip_cost.toString(),
        reduced_cost = "",
        wastage_ywae = wastage_ywae.toString(),
        rebuyPrice = "",
        priceForPawn = "",
        calculatedPriceForPawn = "",

        oldStockCondition = "",
        oldStockGQinCarat = "",
        oldStockImpurityWeightY = "",

        oldStockABuyingPrice = "",
        oldStockb_voucher_buying_value = "",
        oldStockc_voucher_buying_value = "",
        oldStockDGoldWeightY = "",
        oldStockEPriceFromNewVoucher = "",
        oldStockFVoucherShownGoldWeightY = "",
    )
}

@Entity(tableName = "shweMiFile")
data class ShweMiFileEntity(
    val id: String?,
    val type: String?,
    val url: String?
)


fun ShweMiFile.asEntity(): ShweMiFileEntity {
    return ShweMiFileEntity(
        id, type, url
    )
}


fun ShweMiFileEntity.asDomain(): ShweMiFile {
    return ShweMiFile(
        id, type, url
    )
}
/** ui matching */
// productIdList:List<MultipartBody.Part>?,// done
// user_id:MultipartBody.Part?,// done
// paid_amount:MultipartBody.Part?, //unclear
// reduced_cost:MultipartBody.Part?,//done (calculate from wastage ywae)
// old_voucher_paid_amount:MultipartBody.Part?,//အဝယ်ဘောင်ချာမှစျေး
// old_stocks_nameList:List<MultipartBody.Part>?,//default
// oldStockImageIds:List<MultipartBody.Part>?,//done (image)
// oldStockImageFile:List<MultipartBody.Part>?,//done
// oldStockCondition:List<MultipartBody.Part>?,//done default
//
// oldStockGemWeightY:List<MultipartBody.Part>?,//done (gem_weight_ywae)
//
// oldStockGoldGemWeightY:List<MultipartBody.Part>?,//done (gold_and_gem_weight_gm)
//
// oldStockImpurityWeightY:List<MultipartBody.Part>?,//done default
//
// oldStockGoldWeightY:List<MultipartBody.Part>?,//done(derived_net_gold_weight_ywae)
//
// oldStockWastageWeightY:List<MultipartBody.Part>?,//done (wastage ywae)
//
// oldStockRebuyPrice:List<MultipartBody.Part>?,//done default
// oldStockGQinCarat:List<MultipartBody.Part>?,//done default
// oldStockMaintenance_cost:List<MultipartBody.Part>?,//done
// oldStockGemValue:List<MultipartBody.Part>?,//done
// oldStockPTAndClipCost:List<MultipartBody.Part>?,//done
// oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,//default
// oldStockPriceForPawn:List<MultipartBody.Part>?,//done
// oldStockCalculatedForPawn:List<MultipartBody.Part>?,//decided pawn price
//
// oldStockABuyingPrice:List<MultipartBody.Part>?,
// oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
// oldStockc_voucher_buying_price:List<MultipartBody.Part>?,
//
// oldStockDGoldWeightK:List<MultipartBody.Part>?,
// oldStockDGoldWeightP:List<MultipartBody.Part>?,
// oldStockDGoldWeightY:List<MultipartBody.Part>?,
//
// oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,
//
// oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?
