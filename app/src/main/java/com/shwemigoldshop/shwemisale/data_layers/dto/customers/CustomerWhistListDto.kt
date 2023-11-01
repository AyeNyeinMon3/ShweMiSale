package com.shwemigoldshop.shwemisale.data_layers.dto.customers

import com.shwemigoldshop.shwemisale.data_layers.domain.customers.CustomerWhistListDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.product.ProductDto
import com.shwemigoldshop.shwemisale.data_layers.dto.product.asDomain

data class CustomerWhistListApiResponse(
    val data:List<CustomerWhistListDto>
)

data class CustomerWhistListDto(
    val id:String?,
    val name:String?,
    val image:String?,
    val products:List<ProductDto>?
)

fun CustomerWhistListDto.asDomain():CustomerWhistListDomain{
    return CustomerWhistListDomain(
        id = id.orEmpty(),
        name =name.orEmpty(),
        image = image.orEmpty(),
        product = products?.map { it.asDomain() }
    )
}
