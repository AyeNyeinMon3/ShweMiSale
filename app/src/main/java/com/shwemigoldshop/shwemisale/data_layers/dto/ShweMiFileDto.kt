package com.shwemigoldshop.shwemisale.data_layers.dto

import com.shwemigoldshop.shwemisale.data_layers.domain.ShweMiFileDomain

data class ShweMiFileDto(
    val id:String?,
    val type:String?,
    val url:String?
)

fun ShweMiFileDto.asDomain():ShweMiFileDomain{
    return ShweMiFileDomain(id = id.orEmpty(),type = type.orEmpty(), url = url.orEmpty())
}
