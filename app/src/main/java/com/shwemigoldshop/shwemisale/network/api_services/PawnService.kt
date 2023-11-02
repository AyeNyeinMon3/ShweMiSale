package com.shwemigoldshop.shwemisale.network.api_services

import com.shwemigoldshop.shwemisale.data_layers.dto.RemainingAmountResponse
import com.shwemigoldshop.shwemisale.data_layers.dto.SimpleResponseWithDataString
import com.shwemigoldshop.shwemisale.data_layers.dto.pawn.PawnInterestRateApiResponse
import com.shwemigoldshop.shwemisale.data_layers.dto.pawn.PawnVoucherScanResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PawnService {

    @GET("api/pawn/interest-rate")
    suspend fun getPawnInterestRate(
        @Header("Authorization") token:String,
        @Query("user_id")userId:String,
        @Query("amount")amount:String
        ):Response<PawnInterestRateApiResponse>

    @GET("api/pawn/{voucherCode}/scan")
    suspend fun getPawnVoucherScan(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
    ):Response<PawnVoucherScanResponse>


    @POST("api/pawn/store")
    @Multipart
    suspend fun storePawn(
        @Header("Authorization") token:String,
        @Part("user_id")user_id:RequestBody?,
        @Part("total_debt_amount")total_debt_amount:RequestBody?,
        @Part("interest_rate")interest_rate:RequestBody?,
        @Part("warning_period_months")warning_period_months:RequestBody?,
        @Part("interest_free_from")interest_free_from:RequestBody?,
        @Part("interest_free_to")interest_free_to:RequestBody?,
        @Part("is_app_functions_allowed") is_app_functions_allowed:RequestBody?,
        @Part("old_stock_session_key") old_stock_session_key:RequestBody?


    ):Response<SimpleResponseWithDataString>


    @POST("api/pawn/{voucherCode}/create-prepaid-debt")
    @FormUrlEncoded
    suspend fun createPrepaidDebt(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("prepaid_debt") prepaid_debt:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("is_app_functions_allowed") is_app_functions_allowed:String?,
        ):Response<SimpleResponseWithDataString>

    @POST("api/pawn/{voucherCode}/create-prepaid-interest")
    @FormUrlEncoded
    suspend fun createPrepaidInterest(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("number_of_months") number_of_months:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("is_app_functions_allowed") is_app_functions_allowed:String?,

        ):Response<SimpleResponseWithDataString>

    @POST("api/pawn/{voucherCode}/increase-debt")
    @FormUrlEncoded
    suspend fun increaseDebt(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("increased_debt") increased_debt:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("is_app_functions_allowed") is_app_functions_allowed:String?,
        @Field("old_stock_session_key") old_stock_session_key:String?
    ):Response<SimpleResponseWithDataString>

    @POST("api/pawn/{voucherCode}/pay-interest-and-increase-debt")
    @FormUrlEncoded
    suspend fun payInterestAndIncreaseDebt(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("increased_debt") increased_debt:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("is_app_functions_allowed") is_app_functions_allowed:String?,
        @Field("old_stock_session_key") old_stock_session_key:String?
    ):Response<SimpleResponseWithDataString>


    @POST("api/pawn/{voucherCode}/pay-interest")
    @FormUrlEncoded
    suspend fun payInterest(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("is_app_functions_allowed") is_app_functions_allowed:String?,
        ):Response<SimpleResponseWithDataString>

    @POST("api/pawn/{voucherCode}/pay-interest-and-settle-debt")
    @FormUrlEncoded
    suspend fun payInterestAndSettleDebt(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("debt") debt:String,
        @Field("is_app_functions_allowed") is_app_functions_allowed:String?,
        ):Response<SimpleResponseWithDataString>

    @JvmSuppressWildcards
    @POST("api/pawn/{voucherCode}/pay-interest-and-return-stock")
    @FormUrlEncoded
    suspend fun payInterestAndReturnStock(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("debt") debt:String,
        @Field("old_stock_id[]") old_stock_id:List<String>,
        @Field("is_app_functions_allowed") is_app_functions_allowed:String?,
        ):Response<SimpleResponseWithDataString>

    @POST("api/pawn/{voucherCode}/settle")
    @FormUrlEncoded
    suspend fun settle(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("is_app_functions_allowed") is_app_functions_allowed:String?,

        ):Response<SimpleResponseWithDataString>

    @POST("api/pawn/{voucherCode}/sell-old-stocks")
    @FormUrlEncoded
    suspend fun sellOldStock(
        @Header("Authorization") token:String,
        @Path("voucherCode") voucherCode:String,
        @Field("reduced_amount") reduced_amount:String,
        @Field("is_app_functions_allowed") is_app_functions_allowed:String?,
        @Field("old_stock_id[]") old_stock_id:List<String>,
        ):Response<SimpleResponseWithDataString>

    @POST("api/sales/make-payment")
    @FormUrlEncoded
    suspend fun payBalance(
        @Header("Authorization") token:String,
        @Field("sale_id") sale_id:String,
        @Field("paid_amount") paid_amount:String?,
    ):Response<SimpleResponseWithDataString>

    @GET("api/sales/{saleCode}/remaining-amount")
    suspend fun getRemainingAmount(
        @Header("Authorization") token:String,
        @Path("saleCode") saleCode:String?,
    ):Response<RemainingAmountResponse>
}