package com.shwemigoldshop.shwemisale.room_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shwemigoldshop.shwemisale.data_layers.domain.customers.CustomerDataDomain
import com.shwemigoldshop.shwemisale.data_layers.ui_models.customers.CustomerDataUiModel

@Entity(tableName = "customer")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val phone: String,
    val date_of_birth: String,
    val gender: String,
    val address: String,
    val province_id: String,
    val township_id: String,
    val province_name: String,
    val township_name: String,
    val nrc: String
)

fun CustomerDataDomain.asEntity(): CustomerEntity {
    return CustomerEntity(
        id,
        code,
        name,
        phone,
        date_of_birth,
        gender,
        address,
        province_id,
        township_id,
        province_name,
        township_name,
        nrc
    )
}

fun CustomerEntity.asUiModel(): CustomerDataUiModel {
    return CustomerDataUiModel(
        id,
        code,
        name,
        phone,
        date_of_birth,
        gender,
        address,
        province_id,
        township_id,
        province_name,
        township_name,
        nrc
    )
}
