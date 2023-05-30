package com.example.shwemisale.repository

import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.customers.CustomerDataDomain
import com.example.shwemisale.data_layers.domain.customers.CustomerWhistListDomain
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.customers.CustomerDataDto
import com.example.shwemisale.data_layers.dto.customers.CustomerDataResponse
import com.example.shwemisale.data_layers.dto.customers.ProvinceDto
import com.example.shwemisale.data_layers.dto.customers.TownshipDto
import retrofit2.http.Field
import retrofit2.http.Query


interface CustomerRepository {
    suspend fun getCustomerDataByCode(
        code: String?,
        name: String?,
        phone: String?,
        date_of_birth: String?,
        gender: String?,
        province_id: String?,
        township_id: String?,
        address: String?,
        nrc: String?,
    ): Resource<CustomerDataResponse>

    suspend fun getCustomerWhistList(customerId: String): Resource<List<CustomerWhistListDomain>>

    suspend fun getProvince(): Resource<List<ProvinceDto>>
    suspend fun getTownship(province_id: String): Resource<List<TownshipDto>>

    suspend fun addNewUser(
        name: String,
        phone: String,
        date_of_birth: String?,
        gender: String,
        province_id: String,
        township_id: String,
        address: String,
        nrc: String?,
    ):Resource<CustomerDataDomain>

}