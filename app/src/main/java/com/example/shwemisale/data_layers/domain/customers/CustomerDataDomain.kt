package com.example.shwemisale.data_layers.domain.customers

import com.example.shwemisale.data_layers.ui_models.customers.CustomerDataUiModel

data class CustomerDataDomain(
    val id:String,
    val code:String,
    val name:String,
    val phone:String,
    val date_of_birth:String,
    val gender:String,
    val address:String,
    val province_id:String,
    val township_id:String,
    val province_name:String,
    val township_name:String,
    val nrc:String
)

fun CustomerDataDomain.asUiModel(): CustomerDataUiModel {
    return CustomerDataUiModel(
        id, code, name, phone, date_of_birth, gender, address, province_id, township_id, province_name, township_name,nrc
    )
}
