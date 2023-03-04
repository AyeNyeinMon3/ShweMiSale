package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceApiResponse
import com.example.shwemisale.data_layers.dto.customers.CustomerWhistListApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CalculationService {
    @GET("api/gold-types")
    suspend fun getGoldTypePrice(
        @Header("Authorization") token:String,
        @Query("id") goldTypeId:String,
    ): Response<GoldTypePriceApiResponse>
}