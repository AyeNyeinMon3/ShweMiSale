package com.shwemigoldshop.shwemisale.data_layers.ui_models.product

data class ProductSizeAndReasonUiModel(
    val size:List<ProductSizeUiModel>,
    val reasons:List<ProductReasonUiModel>
)
data class ProductSizeUiModel(
    val id:String,
    val quantity:String
)

data class ProductReasonUiModel(
    val id:String,
    val jewellery_type_id:String,
    val reason:String,
    val general_sale_item_id:String?,
    val is_clip_change:String
)


