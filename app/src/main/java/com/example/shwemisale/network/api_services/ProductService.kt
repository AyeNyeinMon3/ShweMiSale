package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.dto.SimpleResponseWithDataString
import com.example.shwemisale.data_layers.dto.product.*
import retrofit2.Response
import retrofit2.http.*

interface ProductService {

    @GET("api/products/{productId}/info-for-edit")
    suspend fun getProductInfo(
        @Header("Authorization") token:String,
        @Path("productId")productId:String
    ):Response<ProductInfoApiResponse>

    @GET("api/products/{productCode}/scan")
    suspend fun getProductId(
        @Header("Authorization") token:String,
        @Path("productCode")productCode:String
    ):Response<ProductIdResponse>

    @GET("api/products/{productCode}/exchange-id")
    suspend fun exchangeProductCodeWithId(
        @Header("Authorization") token:String,
        @Path("productCode")productCode:String
    ):Response<ProductIdResponse>

    @POST("api/products/{productId}/remove-temp")
    suspend fun removeTemp(
        @Header("Authorization") token:String,
        @Path("productId")productId:String
    ):Response<SessionKeyResponse>

    @GET("api/products/{productId}/sizes-and-reasons")
    suspend fun getProductSizeAndReason(
        @Header("Authorization") token:String,
        @Path("productId")productId:String
    ):Response<ProductSizeAndReasonApiResponse>

    @FormUrlEncoded
    @POST("api/products/{productId}/update")
    suspend fun updateProductInfo(
        @Header("Authorization") token:String,
        @Path("productId")productId:String,
        @Field("gold_and_gem_weight_gm")gold_and_gem_weight_gm:String?,
        @Field("gem_weight_ywae")gem_weight_ywae:String?,
        @Field("wastage_ywae")wastage_ywae:String?,
        @Field("gem_value")gem_value:String?,
        @Field("promotion_discount")promotion_discount:String?,
        @Field("jewellery_type_size_id")jewellery_type_size_id:String?,
        @Field("edit_reason_id")edit_reason_id:String?,
        @Field("pt_and_clip_cost")pt_and_clip_cost:String?,
        @Field("maintenance_cost")maintenance_cost:String?,
        @Field("general_sale_item_id")general_sale_item_id:String?,
        @Field("new_clip_wt_gm")new_clip_wt_gm:String?,
        @Field("old_clip_wt_gm")old_clip_wt_gm:String?,
        ):Response<SimpleResponseWithDataString>
}