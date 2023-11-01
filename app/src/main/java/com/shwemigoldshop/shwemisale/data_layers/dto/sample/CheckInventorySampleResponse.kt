package com.shwemigoldshop.shwemisale.data_layers.dto.sample

import com.shwemigoldshop.shwemisale.data_layers.domain.sample.SampleDomain


data class CheckInventorySampleResponse(
    val data: SampleDto
)

data class SampleDto(
    val id: String?,
    val box_code: String?,
    val name: String?,
    val product_code: String?,
    val product_id: String?,
    var specification: String?,
    val thumbnail: String?,
    val weight_gm: String?
)

fun SampleDto.asDomain(isInventory:Boolean):SampleDomain{
    return SampleDomain(
        null,id,box_code,name,product_code,product_id,specification,thumbnail,weight_gm, isNew = specification.isNullOrEmpty(),
        isInventory = isInventory
    )
}