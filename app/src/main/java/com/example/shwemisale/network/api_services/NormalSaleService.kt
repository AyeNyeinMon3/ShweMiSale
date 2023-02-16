package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.dto.calculation.GoldPriceResponse
import com.example.shwemisale.data_layers.dto.customers.CustomerWhistListApiResponse
import com.example.shwemisale.data_layers.dto.voucher.PaidAmountOfVoucherResponse
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithKPYResponse
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithValueResponse
import okhttp3.MultipartBody
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

    @POST
    @Multipart
    suspend fun submitWithKPY(
        @Header("Authorization") token:String,
        @Part productIdList:List<MultipartBody.Part>?,
        @Part user_id:MultipartBody.Part?,
        @Part paid_amount:MultipartBody.Part?,
        @Part reduced_cost:MultipartBody.Part?,
        @Part old_voucher_paid_amount:MultipartBody.Part?,
        @Part old_stocks_nameList:List<MultipartBody.Part>?,
        @Part oldStockImageIds:List<MultipartBody.Part>?,
        @Part oldStockImageFile:List<MultipartBody.Part>?,
        @Part oldStockCondition:List<MultipartBody.Part>?,
        @Part oldStockGemWeightK:List<MultipartBody.Part>?,
        @Part oldStockGemWeightP:List<MultipartBody.Part>?,
        @Part oldStockGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockGoldGemWeightK:List<MultipartBody.Part>?,
        @Part oldStockGoldGemWeightP:List<MultipartBody.Part>?,
        @Part oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        @Part oldStockImpurityWeightK:List<MultipartBody.Part>?,
        @Part oldStockImpurityWeightP:List<MultipartBody.Part>?,
        @Part oldStockImpurityWeightY:List<MultipartBody.Part>?,

        @Part oldStockGoldWeightK:List<MultipartBody.Part>?,
        @Part oldStockGoldWeightP:List<MultipartBody.Part>?,
        @Part oldStockGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockWastageWeightK:List<MultipartBody.Part>?,
        @Part oldStockWastageWeightP:List<MultipartBody.Part>?,
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

        @Part oldStockDGoldWeightK:List<MultipartBody.Part>?,
        @Part oldStockDGoldWeightP:List<MultipartBody.Part>?,
        @Part oldStockDGoldWeightY:List<MultipartBody.Part>?,

        @Part oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        @Part oldStockFVoucherShownGoldWeightK:List<MultipartBody.Part>?,
        @Part oldStockFVoucherShownGoldWeightP:List<MultipartBody.Part>?,
        @Part oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,

        )
}