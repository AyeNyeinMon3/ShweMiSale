package com.example.shwemisale.data_layers.ui_models.goldFromHome

import com.example.shwemisale.data_layers.ShweMiFile
import retrofit2.http.Part

data class StockFromHomeInfoUiModel(
    val id: String,
    val code: String?,
    val qty:String?,
    val size:String?,
    val derived_gold_type_id: String?,

    val derived_net_gold_weight_kpy: String?,

    val derived_net_gold_weight_ywae: String?,

    val gem_value: String?,


    val gem_weight_ywae: String?,
    val goldWeightYwae:String?,

    val gold_and_gem_weight_gm: String?,

    val gold_price: String?,
    val image: String?,
    val imageId: String?,
    val maintenance_cost: String?,
    val name: String?,
    val pt_and_clip_cost: String?,
    val reduced_cost:String?,


    val wastage_ywae: String?,

    val rebuyPrice:String?,
    val priceForPawn:String?,//rebuy - pawn diff
    val calculatedPriceForPawn:String?,//PawnPrice =  (GoldWeight(Ywae)+Htae Pay ayawt(Ywae)->(Kyat))*ThetMhatPawnPrice + otherCostFromUI(only one for gem Discount)

    val oldStockCondition:String?,
    val oldStockGQinCarat:String?,
    val oldStockImpurityWeightY:String?,


    val oldStockABuyingPrice:String?,
    val oldStockb_voucher_buying_value:String?,
    val oldStockc_voucher_buying_value:String?,

    val oldStockDGoldWeightY:String?,
    val oldStockEPriceFromNewVoucher:String?,
    val oldStockFVoucherShownGoldWeightY:String?,

    )
//
//@Part productIdList:List<MultipartBody.Part>?,
//@Part user_id:MultipartBody.Part?,
//@Part paid_amount:MultipartBody.Part?,
//@Part reduced_cost:MultipartBody.Part?,
//@Part old_voucher_paid_amount:MultipartBody.Part?,
//@Part old_stocks_nameList:List<MultipartBody.Part>?,
//@Part oldStockImageIds:List<MultipartBody.Part>?,
//@Part oldStockImageFile:List<MultipartBody.Part>?,
//@Part oldStockCondition:List<MultipartBody.Part>?,
//
//@Part oldStockGemWeightY:List<MultipartBody.Part>?,
//
//@Part oldStockGoldGemWeightY:List<MultipartBody.Part>?,
//
//@Part oldStockImpurityWeightY:List<MultipartBody.Part>?,
//
//@Part oldStockGoldWeightY:List<MultipartBody.Part>?,
//
//@Part oldStockWastageWeightY:List<MultipartBody.Part>?,
//
//@Part oldStockRebuyPrice:List<MultipartBody.Part>?,
//@Part oldStockGQinCarat:List<MultipartBody.Part>?,
//@Part oldStockMaintenance_cost:List<MultipartBody.Part>?,
//@Part oldStockGemValue:List<MultipartBody.Part>?,
//@Part oldStockPTAndClipCost:List<MultipartBody.Part>?,
//@Part oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
//@Part oldStockPriceForPawn:List<MultipartBody.Part>?,
//@Part oldStockCalculatedForPawn:List<MultipartBody.Part>?,//decided pawn price
//
//@Part oldStockABuyingPrice:List<MultipartBody.Part>?,
//@Part oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
//@Part oldStockc_voucher_buying_price:List<MultipartBody.Part>?,
//
//@Part oldStockDGoldWeightK:List<MultipartBody.Part>?,
//@Part oldStockDGoldWeightP:List<MultipartBody.Part>?,
//@Part oldStockDGoldWeightY:List<MultipartBody.Part>?,
//
//@Part oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,
//
//@Part oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?