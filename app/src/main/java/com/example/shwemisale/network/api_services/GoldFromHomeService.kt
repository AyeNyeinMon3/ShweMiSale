package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemsResponse
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceApiResponse
import com.example.shwemisale.data_layers.dto.customers.TownshipApiResponse
import com.example.shwemisale.data_layers.dto.goldFromHome.PawnDiffValueResponse
import com.example.shwemisale.data_layers.dto.goldFromHome.RebuyPriceResponse
import com.example.shwemisale.data_layers.dto.goldFromHome.StockFromeHomeInfoResponse
import com.example.shwemisale.data_layers.dto.goldFromHome.StockWeightByVoucherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GoldFromHomeService {

    @GET("api/sales/{voucherCode}/product-weights")
    suspend fun getStockWeightByVoucher(
        @Header("Authorization") token: String,
        @Path("voucherCode") voucherCode: String,
    ): Response<StockWeightByVoucherResponse>

    @GET("api/sales/{voucherCode}/products-info")
    suspend fun getStockInfoByVoucher(
        @Header("Authorization") token: String,
        @Path("voucherCode") voucherCode: String,
        @Query("product_id[]") productIdList: List<String>
    ): Response<StockFromeHomeInfoResponse>

    @GET("api/rebuy-price")
    suspend fun getReBuyPrice(
        @Header("Authorization") token: String,
        @Query("horizontal_option_name") horizontal_option_name:String,
        @Query("vertical_option_name") vertical_option_name:String,
        @Query("size") size:String
        ):Response<RebuyPriceResponse>

    @GET("api/rebuy-items")
    suspend fun getRebuyItems(
        @Header("Authorization") token: String,
        @Query("size") size:String
    ):Response<RebuyItemsResponse>

    @GET("api/pawn/get-pawn-diff-value")
    suspend fun getPawnDiffValue(
        @Header("Authorization") token: String,
        ):Response<PawnDiffValueResponse>
}