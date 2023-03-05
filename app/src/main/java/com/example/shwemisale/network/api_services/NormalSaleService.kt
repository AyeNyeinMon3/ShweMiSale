package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.dto.GeneralSaleApiResponse
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.calculation.GoldPriceResponse
import com.example.shwemisale.data_layers.dto.customers.CustomerWhistListApiResponse
import com.example.shwemisale.data_layers.dto.sample.CheckInventorySampleResponse
import com.example.shwemisale.data_layers.dto.voucher.PaidAmountOfVoucherResponse
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithKPYResponse
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithValueResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface NormalSaleService {
    @GET("api/sales/normal/{voucherCode}/paid-amount")
    suspend fun getPaidAmountOfVoucher(
        @Header("Authorization") token:String,
        @Path("voucherCode")voucherCode:String
    ): Response<PaidAmountOfVoucherResponse>

    @GET("api/sales/normal/get-gold-price")
    suspend fun getGoldPrices(
        @Header("Authorization") token:String,
        @Query("product_id[]")productIdList:List<String>
    ): Response<GoldPriceResponse>

    @GET("api/sales/normal/get-voucher-info-kpy")
    suspend fun getVoucherInfoWithKPY(
        @Header("Authorization") token:String,
        @Query("product_id[]")productIdList:List<String>
    ): Response<VoucherInfoWithKPYResponse>

    @GET("api/sales/normal/get-voucher-info-value")
    suspend fun getVoucherInfoWithValue(
        @Header("Authorization") token:String,
        @Query("product_id[]")productIdList:List<String>
    ): Response<VoucherInfoWithValueResponse>

    @JvmSuppressWildcards
    @POST("api/sales/normal/store/kpy")
    @Multipart
    suspend fun submitWithKPY(
        @Header("Authorization") token:String,
        @Part productIdList:List<MultipartBody.Part>?,
        @Part("user_id") user_id:RequestBody?,
        @Part("paid_amount") paid_amount:RequestBody?,
        @Part("reduced_cost") reduced_cost:RequestBody?,

        @Part old_voucher_paid_amount:MultipartBody.Part?,
        @Part old_stocks_nameList:List<MultipartBody.Part>?,

        @Part oldStockImageIds:List<MultipartBody.Part>?,
        @Part oldStockImageFile:List<MultipartBody.Part>?,
        @Part oldStockCondition:List<MultipartBody.Part>?,
        @Part old_stock_qty:List<MultipartBody.Part>?,
        @Part old_stock_size:List<MultipartBody.Part>?,
        @Part oldStockGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockImpurityWeightY:List<MultipartBody.Part>?,

        @Part oldStockGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockWastageWeightY:List<MultipartBody.Part>?,

        @Part oldStockRebuyPrice:List<MultipartBody.Part>?,
        @Part oldStockGQinCarat:List<MultipartBody.Part>?,
        @Part oldStockMaintenance_cost:List<MultipartBody.Part>?,
        @Part oldStockGemValue:List<MultipartBody.Part>?,
        @Part oldStockPTAndClipCost:List<MultipartBody.Part>?,
        @Part oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        @Part oldStockPriceForPawn:List<MultipartBody.Part>?,
        @Part oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        @Part oldStockABuyingPrice:List<MultipartBody.Part>?,
        @Part oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        @Part oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        @Part oldStockDGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        @Part oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,

        ):Response<SimpleResponse>

    @POST("api/sales/normal/store/value")
    @Multipart
    suspend fun submitWithValue(
        @Header("Authorization") token:String,
        @Part productIdList:List<MultipartBody.Part>?,
        @Part("user_id") user_id:RequestBody?,
        @Part("paid_amount") paid_amount:RequestBody?,
        @Part("reduced_cost") reduced_cost:RequestBody?,

        @Part old_voucher_paid_amount:MultipartBody.Part?,
        @Part old_stocks_nameList:List<MultipartBody.Part>?,
        @Part old_stocks_gem_details_gem_qty:List<MultipartBody.Part>?,
        @Part old_stocks_gem_details_gem_weight_gm_per_unit:List<MultipartBody.Part>?,
        @Part old_stocks_gem_details_gem_weight_ywae_per_unit:List<MultipartBody.Part>?,
//        old_stocks[0][gem_weight_details][0][gem_qty]:5
//    old_stocks[0][gem_weight_details][0][gem_weight_gm_per_unit]:5.78
//    old_stocks[0][gem_weight_details][0][gem_weight_ywae_per_unit]:56.98

        @Part oldStockImageIds:List<MultipartBody.Part>?,
        @Part oldStockImageFile:List<MultipartBody.Part>?,
        @Part oldStockCondition:List<MultipartBody.Part>?,
        @Part old_stock_qty:List<MultipartBody.Part>?,
        @Part old_stock_size:List<MultipartBody.Part>?,
        @Part oldStockGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockImpurityWeightY:List<MultipartBody.Part>?,

        @Part oldStockGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockWastageWeightY:List<MultipartBody.Part>?,

        @Part oldStockRebuyPrice:List<MultipartBody.Part>?,
        @Part oldStockGQinCarat:List<MultipartBody.Part>?,
        @Part oldStockMaintenance_cost:List<MultipartBody.Part>?,
        @Part oldStockGemValue:List<MultipartBody.Part>?,
        @Part oldStockPTAndClipCost:List<MultipartBody.Part>?,
        @Part oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        @Part oldStockPriceForPawn:List<MultipartBody.Part>?,
        @Part oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        @Part oldStockABuyingPrice:List<MultipartBody.Part>?,
        @Part oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        @Part oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        @Part oldStockDGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        @Part oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,

        ):Response<SimpleResponse>


    /** New Order Sales */

    @POST("api/sales/order/store")
    @Multipart
    suspend fun submitOrderSale(
        @Header("Authorization") token:String,
        @Field("name") name:String,
        @Field("gold_type_id") gold_type_id:String,
        @Field("gold_price") gold_price:String,
        @Field("total_gold_weight_ywae") total_gold_weight_ywae:String,
        @Field("est_unit_wastage_ywae") est_unit_wastage_ywae:String,
        @Field("qty") qty:String,
        @Field("gem_value") gem_value:String,
        @Field("maintenance_cost") maintenance_cost:String,
        @Field("date_of_delivery") date_of_delivery:String,
        @Field("is_delivered") is_delivered:String,
        @Field("remark") remark:String,
        @Field("user_id") user_id:String,
        @Field("paid_amount") paid_amount:String,
        @Field("reduced_cost") reduced_cost:String,

        @Part itemsGeneralSaleItemId:List<MultipartBody.Part>?,
        @Part itemsQty:List<MultipartBody.Part>?,
        @Part itemsGoldWeightGm:List<MultipartBody.Part>?,
        @Part itemsWastageYwae:List<MultipartBody.Part>?,
        @Part itemsMaintenanceCost:List<MultipartBody.Part>?,

        @Part old_stocks_nameList:List<MultipartBody.Part>?,
        @Part oldStockImageIds:List<MultipartBody.Part>?,
        @Part oldStockImageFile:List<MultipartBody.Part>?,
        @Part oldStockCondition:List<MultipartBody.Part>?,

        @Part oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockImpurityWeightY:List<MultipartBody.Part>?,

        @Part oldStockGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockWastageWeightY:List<MultipartBody.Part>?,

        @Part oldStockRebuyPrice:List<MultipartBody.Part>?,
        @Part oldStockGQinCarat:List<MultipartBody.Part>?,
        @Part oldStockMaintenance_cost:List<MultipartBody.Part>?,
        @Part oldStockGemValue:List<MultipartBody.Part>?,
        @Part oldStockPTAndClipCost:List<MultipartBody.Part>?,
        @Part oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        @Part oldStockPriceForPawn:List<MultipartBody.Part>?,
        @Part oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        @Part oldStockABuyingPrice:List<MultipartBody.Part>?,
        @Part oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        @Part oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        @Part oldStockDGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        @Part oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockSampleListId:List<MultipartBody.Part>?
        ):Response<SimpleResponse>


    @POST("api/sales/pure-gold/store")
    @Multipart
    suspend fun submitPureGoldSale(
        @Header("Authorization") token:String,
        @Part itemsGoldWeightYwae:List<MultipartBody.Part>?,
        @Part itemsWastageYwae:List<MultipartBody.Part>?,
        @Part itemsMaintenanceCost: List<MultipartBody.Part>?,
        @Part itemsThreadingFees:List<MultipartBody.Part>?,
        @Part itemsType:List<MultipartBody.Part>?,
        @Part("gold_price") gold_price:String,
        @Part("user_id") user_id:String,
        @Part("paid_amount") paid_amount:String,
        @Part("reduced_cost") reduced_cost:String,

        @Part old_stocks_nameList:List<MultipartBody.Part>?,
        @Part oldStockImageIds:List<MultipartBody.Part>?,
        @Part oldStockImageFile:List<MultipartBody.Part>?,
        @Part oldStockCondition:List<MultipartBody.Part>?,

        @Part oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockImpurityWeightY:List<MultipartBody.Part>?,

        @Part oldStockGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockWastageWeightY:List<MultipartBody.Part>?,

        @Part oldStockRebuyPrice:List<MultipartBody.Part>?,
        @Part oldStockGQinCarat:List<MultipartBody.Part>?,
        @Part oldStockMaintenance_cost:List<MultipartBody.Part>?,
        @Part oldStockGemValue:List<MultipartBody.Part>?,
        @Part oldStockPTAndClipCost:List<MultipartBody.Part>?,
        @Part oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        @Part oldStockPriceForPawn:List<MultipartBody.Part>?,
        @Part oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        @Part oldStockABuyingPrice:List<MultipartBody.Part>?,
        @Part oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        @Part oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        @Part oldStockDGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        @Part oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockSampleListId:List<MultipartBody.Part>?
        ):Response<SimpleResponse>

    @POST("api/sales/general/store")
    @FormUrlEncoded
    @Multipart
    suspend fun submitGeneralSale(
        @Header("Authorization") token:String,
        @Part itemsGeneralSaleItemId: List<MultipartBody.Part>?,
        @Part itemsQty: List<MultipartBody.Part>?,
        @Part itemsGoldWeightYwae:List<MultipartBody.Part>?,
        @Part itemsWastageYwae:List<MultipartBody.Part>?,
        @Part itemsMaintenanceCost: List<MultipartBody.Part>?,
        @Field("user_id") user_id:String,
        @Field("paid_amount") paid_amount:String,
        @Field("reduced_cost") reduced_cost:String,

        @Part old_stocks_nameList:List<MultipartBody.Part>?,
        @Part oldStockImageIds:List<MultipartBody.Part>?,
        @Part oldStockImageFile:List<MultipartBody.Part>?,
        @Part oldStockCondition:List<MultipartBody.Part>?,

        @Part oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockImpurityWeightY:List<MultipartBody.Part>?,

        @Part oldStockGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockWastageWeightY:List<MultipartBody.Part>?,

        @Part oldStockRebuyPrice:List<MultipartBody.Part>?,
        @Part oldStockGQinCarat:List<MultipartBody.Part>?,
        @Part oldStockMaintenance_cost:List<MultipartBody.Part>?,
        @Part oldStockGemValue:List<MultipartBody.Part>?,
        @Part oldStockPTAndClipCost:List<MultipartBody.Part>?,
        @Part oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        @Part oldStockPriceForPawn:List<MultipartBody.Part>?,
        @Part oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        @Part oldStockABuyingPrice:List<MultipartBody.Part>?,
        @Part oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        @Part oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        @Part oldStockDGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        @Part oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,

    ):Response<SimpleResponse>


    @GET("api/general-sale-items")
    suspend fun getGeneralSalesItems(
        @Header("Authorization") token:String,
        ):Response<GeneralSaleApiResponse>
    @GET("api/products/{productId}/check-samples")
    suspend fun checkInventorySample(
        @Header("Authorization") token:String,
        @Path("productId")productId:String
    ):Response<CheckInventorySampleResponse>

    @FormUrlEncoded
    @POST("api/samples/inventory/take")
    suspend fun saveSample(
        @Header("Authorization") token: String,
        @FieldMap sample: HashMap<String, String>
    ): Response<SimpleResponse>

    @Multipart
    @POST("api/samples/outside/take")
    suspend fun saveOutsideSample(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody?,
        @Part("weight_gm") weight_gm: RequestBody?,
        @Part("specification") specification: RequestBody?,
        @Part image: MultipartBody.Part
    ):Response<CheckInventorySampleResponse>
}