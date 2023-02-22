package com.example.shwemisale.repository

import com.example.shwemi.network.dto.ResponseDto
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.example.shwemisale.data_layers.domain.product.ProductSizeAndReasonDomain
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.product.ProductInfoApiResponse
import com.example.shwemisale.data_layers.dto.product.ProductInfoDto
import retrofit2.Response
import retrofit2.http.*

interface ProductRepository {
    suspend fun getProductInfo(
        token:String,
        productId:String
    ): Resource<ProductInfoDomain>

    suspend fun getProductSizeAndReason(
        token:String,
        productId:String
    ): Resource<ProductSizeAndReasonDomain>

    suspend fun updateProductInfo(
        token:String,
        productId:String,
        gold_and_gem_weight_gm:String?,
        gem_weight_ywae:String?,
        gem_value:String?,
        promotion_discount:String?,
        jewellery_type_size_id:String?,
        edit_reason_id:String?,
        pt_and_clip_cost:String?,
        general_sale_item_id:String?,
        new_clip_wt_gm:String?,
    ): Resource<ResponseDto>
}