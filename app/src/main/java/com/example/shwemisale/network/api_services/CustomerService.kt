package com.example.shwemisale.network.api_services

import com.example.shwemi.network.dto.auth.ProfileApiResponse
import com.example.shwemisale.data_layers.dto.customers.CustomerDataResponse
import com.example.shwemisale.data_layers.dto.customers.CustomerWhistListApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CustomerService {

    @GET("api/customers/{customerId}/scan")
    suspend fun getCustomerDataByCode(
        @Header("Authorization") token:String,
        @Path("customerId")customerCode:String
    ): Response<CustomerDataResponse>

    @GET("api/customers/{customerId}/wishlist")
    suspend fun getCustomerWhistList(
        @Header("Authorization") token:String,
        @Path("customerId")customerId:String
    ): Response<CustomerWhistListApiResponse>
}