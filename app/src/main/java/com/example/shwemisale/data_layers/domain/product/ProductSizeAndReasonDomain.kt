package com.example.shwemisale.data_layers.domain.product

import com.example.shwemisale.data_layers.dto.product.ProductReasonDto
import com.example.shwemisale.data_layers.dto.product.ProductSizeAndReasonDto
import com.example.shwemisale.data_layers.dto.product.ProductSizeDto
import com.example.shwemisale.data_layers.ui_models.product.ProductReasonUiModel
import com.example.shwemisale.data_layers.ui_models.product.ProductSizeAndReasonUiModel
import com.example.shwemisale.data_layers.ui_models.product.ProductSizeUiModel

data class ProductSizeAndReasonDomain(
    val size:List<ProductSizeDomain>,
    val reasons:List<ProductReasonDomain>
)
data class ProductSizeDomain(
    val id:String,
    val quantity:String
)

data class ProductReasonDomain(
    val id:String,
    val jewellery_type_id:String,
    val reason:String,
    val general_sale_item_id:String,
    val is_clip_change:String
)

fun ProductSizeDomain.asDomain():ProductSizeUiModel{
    return ProductSizeUiModel(
        id = id.orEmpty(),
        quantity = quantity.orEmpty()
    )
}

fun ProductReasonDomain.asDomain():ProductReasonUiModel{
    return ProductReasonUiModel(
        id = id.orEmpty(),
        jewellery_type_id =jewellery_type_id.orEmpty(),
        reason =reason.orEmpty(),
        general_sale_item_id =general_sale_item_id.orEmpty(),
        is_clip_change =is_clip_change.orEmpty(),
    )
}

fun ProductSizeAndReasonDomain.asDomain():ProductSizeAndReasonUiModel{
    return ProductSizeAndReasonUiModel(
        size = size.map { it.asDomain() },
        reasons = reasons.map { it.asDomain() },
    )
}
