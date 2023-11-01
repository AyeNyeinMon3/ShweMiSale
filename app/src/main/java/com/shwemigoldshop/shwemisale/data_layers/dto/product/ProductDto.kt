package com.shwemigoldshop.shwemisale.data_layers.dto.product

import com.shwemigoldshop.shwemisale.data_layers.domain.product.ProductDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.ShweMiFileDto
import com.shwemigoldshop.shwemisale.data_layers.dto.asDomain

data class ProductDto(
    val id:String?,
    val code:String?,
    val name:String?,
    val cost:String?,
    val size:String?,
    val thumbnail:ShweMiFileDto?
)

fun ProductDto.asDomain():ProductDomain{
    return ProductDomain(
        id = id.orEmpty(),
        code = code.orEmpty(),
        name = name.orEmpty(),
        cost = cost.orEmpty(),
        size = size.orEmpty(),
        thumbnail = thumbnail?.asDomain()
    )
}
