package com.example.shwemisale.data_layers.dto.customers

import com.example.shwemisale.data_layers.domain.customers.CustomerDataDomain


data class CustomerDataResponse(
    val data:CustomerDataDto
)

data class CustomerDataDto(
    val id:String?,
    val code:String?,
    val name:String?,
    val phone:String?,
    val date_of_birth:String?,
    val gender:String?,
    val address:String?,
    val province_id:String?,
    val township_id:String?,
    val province_name:String?,
    val township_name:String?,
)

fun CustomerDataDto.asDomain(): CustomerDataDomain {
    return CustomerDataDomain(
        id =id.orEmpty(),
        code =code.orEmpty(),
        name =name.orEmpty(),
        phone =phone.orEmpty(),
        date_of_birth =date_of_birth.orEmpty(),
        gender =gender.orEmpty(),
        address =address.orEmpty(),
        province_id =province_id.orEmpty(),
        township_id =township_id.orEmpty(),
        province_name =province_name.orEmpty(),
        township_name =township_name.orEmpty(),
    )
}
