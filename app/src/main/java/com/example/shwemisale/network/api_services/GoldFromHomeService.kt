package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemsResponse
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.SimpleResponseWithDataString
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetailResponse
import com.example.shwemisale.data_layers.dto.goldFromHome.PawnDiffValueResponse
import com.example.shwemisale.data_layers.dto.goldFromHome.RebuyPriceResponse
import com.example.shwemisale.data_layers.dto.goldFromHome.StockFromHomeInVoucherResponse
import com.example.shwemisale.data_layers.dto.goldFromHome.StockWeightByVoucherResponse
import com.example.shwemisale.data_layers.dto.product.SessionKeyResponse
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
    ): Response<StockFromHomeInVoucherResponse>

    @GET("api/rebuy-price")
    suspend fun getReBuyPrice(
        @Header("Authorization") token: String,
        @Query("horizontal_option_name") horizontal_option_name: String,
        @Query("vertical_option_name") vertical_option_name: String,
        @Query("size") size: String
    ): Response<RebuyPriceResponse>

    @GET("api/rebuy-items")
    suspend fun getRebuyItems(
        @Header("Authorization") token: String,
        @Query("size") size: String
    ): Response<RebuyItemsResponse>

    @GET("api/pawn/get-pawn-diff-value")
    suspend fun getPawnDiffValue(
        @Header("Authorization") token: String,
    ): Response<PawnDiffValueResponse>

    @Multipart
    @POST("api/gem_weight_details/create")
    suspend fun createGemWeightDetail(
        @Header("Authorization") token: String,
        @Part("qty") qty: RequestBody,
        @Part("weight_gm_per_unit") weightGmPerUnit: RequestBody,
        @Part("weight_ywae_per_unit") weightYwaePerUnit: RequestBody,
        @Part("session_key") sessionKey: RequestBody?,
    ): Response<SimpleResponseWithDataString>
    @Multipart
    @POST("api/gem_weight_details/update")
    suspend fun updateGemWeightDetail(
        @Header("Authorization") token: String,
        @Part("id") id: RequestBody,
        @Part("qty") qty: RequestBody,
        @Part("weight_gm_per_unit") weightGmPerUnit: RequestBody,
        @Part("weight_ywae_per_unit") weightYwaePerUnit: RequestBody,
    ): Response<SimpleResponse>

    @GET("api/gem_weight_details")
    suspend fun getGemWeightDetail(
        @Header("Authorization") token: String,
        @Query("session_key")sessionKey:String
    ): Response<GemWeightDetailResponse>
    @POST("api/gem_weight_details/{id}/delete")
    suspend fun deleteGemWeightDetail(
        @Header("Authorization") token: String,
        @Path("id")id:String
    ): Response<SimpleResponse>
}