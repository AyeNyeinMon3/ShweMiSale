package com.shwemigoldshop.shwemisale.data_layers.dto.product

import com.shwemigoldshop.shwemisale.data_layers.domain.product.ProductInfoDomain

data class ProductInfoApiResponse(
    val data: ProductInfoDto
)

data class ProductInfoDto(
    val id: String?,
    val code: String?,
    val name: String?,
    val jewellery_type_id: String?,
    val size: String?,
    val bonus: String?,
    val gold_and_gem_weight_gm: String?,
    val gold_and_gem_weight_gm_old: String?,
    val gem_weight_ywae: String?,
    val gem_value: String?,
    val promotion_discount: String?,
    val pt_and_clip_cost: String?,
    val maintenance_cost: String?,
    val wastage_ywae: String?,
    val cost: String?,
    val gold_type_id: String?,
    var edit_reason_id: String?,
    var general_sale_item_id: String?,
    var new_clip_wt_gm: String?,
    var old_clip_wt_gm: String?,
    var is_order_sale: String?,
    var order_sale_gold_price: String?,
    var order_sale_code: String?,
    val edited_gold_price:String?,
    val image: String?
)

//is_order_sale (0/1)
//order_sale_gold_price
fun ProductInfoDto.asDomain(): ProductInfoDomain {
    return ProductInfoDomain(
        id = id.orEmpty(),
        code = code.orEmpty(),
        name = name.orEmpty(),
        jewellery_type_id = jewellery_type_id.orEmpty(),
        size = size.orEmpty(),
        bonus = bonus.orEmpty(),
        gold_and_gem_weight_gm = gold_and_gem_weight_gm.orEmpty(),
        old_gold_and_gem_weight_gm = gold_and_gem_weight_gm_old.orEmpty(),
        gem_weight_ywae = gem_weight_ywae.orEmpty(),
        gem_value = gem_value.orEmpty(),
        promotion_discount = promotion_discount.orEmpty(),
        pt_and_clip_cost = pt_and_clip_cost.orEmpty(),
        maintenance_cost = maintenance_cost.orEmpty(),
        wastage_ywae = wastage_ywae.orEmpty(),
        cost = cost.orEmpty(),
        gold_type_id = gold_type_id.orEmpty(),
        edit_reason_id = edit_reason_id.orEmpty(),
        general_sale_item_id = general_sale_item_id.orEmpty(),
        new_clip_wt_gm = new_clip_wt_gm.orEmpty(),
        old_clip_wt_gm = old_clip_wt_gm.orEmpty(),
        is_order_sale,
        order_sale_gold_price,
        order_sale_code,
        edited_gold_price,
        image = image.orEmpty()
    )
}

data class ProductIdResponse(
    val data: ProductIdDto
)

data class ProductIdDto(
    val id: String,
)
