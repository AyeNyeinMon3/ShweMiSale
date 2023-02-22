package com.example.shwemisale.data_layers.dto.product

import com.example.shwemisale.data_layers.domain.product.ProductReasonDomain
import com.example.shwemisale.data_layers.domain.product.ProductSizeAndReasonDomain
import com.example.shwemisale.data_layers.domain.product.ProductSizeDomain

data class ProductSizeAndReasonApiResponse(
     val data:ProductSizeAndReasonDto
)

data class ProductSizeAndReasonDto(
    val size:List<ProductSizeDto>,
    val reasons:List<ProductReasonDto>
)
data class ProductSizeDto(
    val id:String?,
    val quantity:String?
)

data class ProductReasonDto(
    val id:String?,
    val jewellery_type_id:String?,
    val reason:String?,
    val general_sale_item_id:String?,
    val is_clip_change:String?
)

fun ProductSizeDto.asDomain():ProductSizeDomain{
    return ProductSizeDomain(
        id = id.orEmpty(),
        quantity = quantity.orEmpty()
    )
}

fun ProductReasonDto.asDomain():ProductReasonDomain{
    return ProductReasonDomain(
        id = id.orEmpty(),
       jewellery_type_id =jewellery_type_id.orEmpty(),
        reason =reason.orEmpty(),
        general_sale_item_id =general_sale_item_id.orEmpty(),
        is_clip_change =is_clip_change.orEmpty(),
    )
}

fun ProductSizeAndReasonDto.asDomain():ProductSizeAndReasonDomain{
    return ProductSizeAndReasonDomain(
        size = size.map { it.asDomain() },
        reasons = reasons.map { it.asDomain() },
    )
}