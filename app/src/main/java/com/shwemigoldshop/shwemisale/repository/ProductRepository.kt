package com.shwemigoldshop.shwemisale.repository

import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.product.ProductSizeAndReasonDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldTypePriceDto

interface ProductRepository {
    suspend fun getProductInfo(
        productId:String
    ): Resource<ProductInfoDomain>

    suspend fun getProductId(
        productCode:String
    ): Resource<String>

    suspend fun exChangeProductCodeWithId(
        productCode:String
    ): Resource<String>

    suspend fun removeTemp(
        productId:String
    ): Resource<String>

    suspend fun getProductSizeAndReason(
        productId:String
    ): Resource<ProductSizeAndReasonDomain>

    suspend fun updateProductInfo(
        productId:String,
        gold_and_gem_weight_gm:String?,
        gem_weight_ywae:String?,
        wastage_ywae:String?,
        gem_value:String?,
        promotion_discount:String?,
        jewellery_type_size_id:String?,
        edit_reason_id:String?,
        pt_and_clip_cost:String?,
        maintenance_cost:String?,
        general_sale_item_id:String?,
        new_clip_wt_gm:String?,
        old_clip_wt_gm:String?,
        edited_gold_price:String?,
    ): Resource<String>

    suspend fun getGoldType(goldTypeId:String): Resource<List<GoldTypePriceDto>>

}