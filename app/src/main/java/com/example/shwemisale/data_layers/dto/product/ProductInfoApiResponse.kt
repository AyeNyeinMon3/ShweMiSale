package com.example.shwemisale.data_layers.dto.product

import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain

data class ProductInfoApiResponse(
    val data:ProductInfoDto
)

data class ProductInfoDto(
    val id:String?,
    val code:String?,
    val name:String?,
    val jewellery_type_id:String?,
    val size:String?,
    val gold_and_gem_weight_gm:String?,
    val gem_weight_ywae:String?,
    val gem_value:String?,
    val promotion_discount:String?,
    val pt_and_clip_cost:String?,
    val maintenance_cost:String?,
)
fun ProductInfoDto.asDomain():ProductInfoDomain{
    return ProductInfoDomain(
        id = id.orEmpty(),
        code = code.orEmpty(),
        name = name.orEmpty(),
        jewellery_type_id = jewellery_type_id.orEmpty(),
        size = size.orEmpty(),
        gold_and_gem_weight_gm = gold_and_gem_weight_gm.orEmpty(),
        gem_weight_ywae = gem_weight_ywae.orEmpty(),
        gem_value = gem_value.orEmpty(),
        promotion_discount = promotion_discount.orEmpty(),
        pt_and_clip_cost = pt_and_clip_cost.orEmpty(),
        maintenance_cost = maintenance_cost.orEmpty(),
    )
}
