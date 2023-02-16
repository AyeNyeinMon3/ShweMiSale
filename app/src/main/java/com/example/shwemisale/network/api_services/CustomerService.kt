package com.example.shwemisale.network.api_services

import com.example.shwemi.network.dto.auth.ProfileApiResponse
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.customers.*
import retrofit2.Response
import retrofit2.http.*

interface CustomerService {

    @GET("api/customers/search")
    suspend fun searchCustomerData(
        @Header("Authorization") token:String?,
        @Query("code") code:String?,
        @Query("name") name:String?,
        @Query("phone") phone:String?,
        @Query("date_of_birth") date_of_birth:String?,
        @Query("gender") gender:String?,
        @Query("province_id") province_id:String?,
        @Query("township_id") township_id:String?,
        @Query("address") address:String?,
        @Query("nrc") nrc:String?,
    ): Response<CustomerDataResponse>

    @GET("api/customers/{customerId}/wishlist")
    suspend fun getCustomerWhistList(
        @Header("Authorization") token:String,
        @Path("customerId")customerId:String
    ): Response<CustomerWhistListApiResponse>


    @GET("api/provinces")
    suspend fun getProvince(
        @Header("Authorization") token:String,
    ): Response<ProvinceApiResponse>


    @GET("api/townships")
    suspend fun getTownship(
        @Header("Authorization") token:String,
    ): Response<TownshipApiResponse>

    @FormUrlEncoded
    @POST("api/customers/store")
    suspend fun addNewUser(
        @Header("Authorization") token:String,
        @Field("name") name:String,
        @Field("phone") phone:String,
        @Field("date_of_birth") date_of_birth:String,
        @Field("gender") gender:String,
        @Field("province_id") province_id:String,
        @Field("township_id") township_id:String,
        @Field("address") address:String,
        @Field("nrc") nrc:String,
    ): Response<AddNewCustomerResponse>
}