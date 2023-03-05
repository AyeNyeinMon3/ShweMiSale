package com.example.shwemisale.repository

import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.dto.GeneralSaleApiResponse
import com.example.shwemisale.data_layers.dto.GeneralSaleDto
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.calculation.GoldPriceDto
import com.example.shwemisale.data_layers.dto.sample.SampleDto
import com.example.shwemisale.data_layers.dto.voucher.PaidAmountOfVoucherResponse
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithKPYDto
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithValueResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface NormalSaleRepository {
    suspend fun getPaidAmountOfVoucher(
        voucherCode: String
    ): Resource<String>

    suspend fun getGoldPrices(
        productIdList: List<String>
    ): Resource<GoldPriceDto>

    suspend fun getVoucherInfoWithKPY(
        productIdList: List<String>
    ): Resource<VoucherInfoWithKPYDto>

    suspend fun getVoucherInfoWithValue(
        productIdList: List<String>
    ): Resource<VoucherInfoWithValueResponse>

    suspend fun submitWithKPY(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,

        old_voucher_paid_amount: MultipartBody.Part?,
        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        old_stock_qty: List<MultipartBody.Part>?,
        old_stock_size: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,

        oldStockGoldGemWeightY: List<MultipartBody.Part>?,

        oldStockImpurityWeightY: List<MultipartBody.Part>?,

        oldStockGoldWeightY: List<MultipartBody.Part>?,

        oldStockWastageWeightY: List<MultipartBody.Part>?,

        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,

        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,

        oldStockDGoldWeightY: List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,

        ): Resource<String>


    suspend fun submitWithValue(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,

        old_voucher_paid_amount: MultipartBody.Part?,
        old_stocks_nameList: List<MultipartBody.Part>?,
        old_stocks_gem_details_gem_qty:List<MultipartBody.Part>?,
        old_stocks_gem_details_gem_weight_gm_per_unit:List<MultipartBody.Part>?,
        old_stocks_gem_details_gem_weight_ywae_per_unit:List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        old_stock_qty: List<MultipartBody.Part>?,
        old_stock_size: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,

        oldStockGoldGemWeightY: List<MultipartBody.Part>?,

        oldStockImpurityWeightY: List<MultipartBody.Part>?,

        oldStockGoldWeightY: List<MultipartBody.Part>?,

        oldStockWastageWeightY: List<MultipartBody.Part>?,

        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,

        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,

        oldStockDGoldWeightY: List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,

        ): Resource<String>


    /** New Order Sales */

    suspend fun submitOrderSale(
        name: String,
        gold_type_id: String,
        gold_price: String,
        total_gold_weight_ywae: String,
        est_unit_wastage_ywae: String,
        qty: String,
        gem_value: String,
        maintenance_cost: String,
        date_of_delivery: String,
        is_delivered: String,
        remark: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,

        itemsGeneralSaleItemId: List<MultipartBody.Part>?,
        itemsQty: List<MultipartBody.Part>?,
        itemsGoldWeightGm: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,

        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,

        oldStockGoldGemWeightY: List<MultipartBody.Part>?,

        oldStockGemWeightY: List<MultipartBody.Part>?,

        oldStockImpurityWeightY: List<MultipartBody.Part>?,

        oldStockGoldWeightY: List<MultipartBody.Part>?,

        oldStockWastageWeightY: List<MultipartBody.Part>?,

        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,

        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,

        oldStockDGoldWeightY: List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,

        oldStockSampleListId: List<MultipartBody.Part>?
    ): Resource<String>


    suspend fun submitPureGoldSale(
        itemsGoldWeightYwae: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,
        itemsThreadingFees: List<MultipartBody.Part>?,
        itemsType: List<MultipartBody.Part>?,
        gold_price: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,

        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,

        oldStockGoldGemWeightY: List<MultipartBody.Part>?,

        oldStockGemWeightY: List<MultipartBody.Part>?,

        oldStockImpurityWeightY: List<MultipartBody.Part>?,

        oldStockGoldWeightY: List<MultipartBody.Part>?,

        oldStockWastageWeightY: List<MultipartBody.Part>?,

        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,

        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,

        oldStockDGoldWeightY: List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,

        oldStockSampleListId: List<MultipartBody.Part>?
    ): Resource<String>


    suspend fun submitGeneralSale(
        itemsGeneralSaleItemId: List<MultipartBody.Part>?,
        itemsQty: List<MultipartBody.Part>?,
        itemsGoldWeightYwae: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,

        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,

        oldStockGoldGemWeightY: List<MultipartBody.Part>?,

        oldStockGemWeightY: List<MultipartBody.Part>?,

        oldStockImpurityWeightY: List<MultipartBody.Part>?,

        oldStockGoldWeightY: List<MultipartBody.Part>?,

        oldStockWastageWeightY: List<MultipartBody.Part>?,

        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,

        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,

        oldStockDGoldWeightY: List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,

        ): Resource<String>


    suspend fun getGeneralSalesItems(
    ): Resource<List<GeneralSaleDto>>

    suspend fun checkSample(productId: String): Resource<SampleDto>
    suspend fun saveSample(sample: HashMap<String, String>): Resource<String>
    suspend fun saveOutsideSample(
        name: RequestBody?,
        weight: RequestBody?,
        specification: RequestBody?,
        image: MultipartBody.Part
    ): Resource<SampleDto>
}