package com.example.shwemisale.repository

import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.customers.CustomerDataDomain
import com.example.shwemisale.data_layers.domain.customers.CustomerWhistListDomain


interface CustomerRepository {
    suspend fun getCustomerDataByCode(customerCode:String): Resource<CustomerDataDomain>
    suspend fun getCustomerWhistList(customerId:String): Resource<List<CustomerWhistListDomain>>

}