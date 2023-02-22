package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateApiResponse
import com.example.shwemisale.data_layers.dto.pawn.PawnVoucherScanResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PawnService {

    @GET("api/pawn/interest-rates")
    suspend fun getPawnInterestRate(
        @Header("Authorization") token:String,
        ):Response<PawnInterestRateApiResponse>

    @GET("api/pawn/{voucherCode}/scan")
    suspend fun getPawnVoucherScan(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
    ):Response<PawnVoucherScanResponse>


    @POST("api/pawn/store")
    @FormUrlEncoded
    @Multipart
    suspend fun storePawn(
        @Header("Authorization") token:String,
        @Field("user_id")user_id:String?,
        @Field("total_debt_amount")total_debt_amount:String?,
        @Field("interest_rate")interest_rate:String?,
        @Field("warning_period_months")warning_period_months:String?,
        @Field("interest_free_from")interest_free_from:String?,
        @Field("interest_free_to")interest_free_to:String?,

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


    @POST("api/pawn/{voucherCode}/create-prepaid-debt")
    @FormUrlEncoded
    suspend fun createPrepaidDebt(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("prepaid_debt") prepaid_debt:String,
        @Field("reduced_amount") reduced_amount:String,
    ):Response<SimpleResponse>

    @POST("api/pawn/{voucherCode}/create-prepaid-interest")
    @FormUrlEncoded
    suspend fun createPrepaidInterest(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("number_of_months") number_of_months:String,
        @Field("reduced_amount") reduced_amount:String,
    ):Response<SimpleResponse>

    @POST("api/pawn/{voucherCode}/increase-debt")
    @FormUrlEncoded
    suspend fun increaseDebt(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("increased_debt") increased_debt:String,
        @Field("reduced_amount") reduced_amount:String,

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

    @POST("api/pawn/{voucherCode}/pay-interest-and-increase-debt")
    @FormUrlEncoded
    suspend fun payInterestAndIncreaseDebt(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("increased_debt") increased_debt:String,
        @Field("reduced_amount") reduced_amount:String,

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


    @POST("api/pawn/{voucherCode}/pay-interest")
    @FormUrlEncoded
    suspend fun payInterest(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,
    ):Response<SimpleResponse>

    @POST("api/pawn/{voucherCode}/pay-interest-and-settle-debt")
    @FormUrlEncoded
    suspend fun payInterestAndSettleDebt(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("debt") debt:String,
    ):Response<SimpleResponse>

    @POST("api/pawn/{voucherCode}/pay-interest-and-return-stock")
    @FormUrlEncoded
    suspend fun payInterestAndReturnStock(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("debt") debt:String,
        @Field("old_stock_id") old_stock_id:String,
    ):Response<SimpleResponse>

    @POST("api/pawn/{voucherCode}/settle")
    @FormUrlEncoded
    suspend fun settle(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,
    ):Response<SimpleResponse>

    @POST("api/pawn/{voucherCode}/sell-old-stocks")
    @FormUrlEncoded
    suspend fun sellOldStock(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,

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
}