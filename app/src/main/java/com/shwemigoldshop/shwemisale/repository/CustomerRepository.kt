package com.shwemigoldshop.shwemisale.repository

import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.customers.CustomerDataDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.customers.CustomerWhistListDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.customers.CustomerDataResponse
import com.shwemigoldshop.shwemisale.data_layers.dto.customers.ProvinceDto
import com.shwemigoldshop.shwemisale.data_layers.dto.customers.TownshipDto


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
    ): Resource<CustomerDataDomain>

}