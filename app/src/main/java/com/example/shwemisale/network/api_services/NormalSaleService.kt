package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.dto.calculation.GoldPriceResponse
import com.example.shwemisale.data_layers.dto.customers.CustomerWhistListApiResponse
import com.example.shwemisale.data_layers.dto.voucher.PaidAmountOfVoucherResponse
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithKPYResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface NormalSaleService {
    @GET("api/sales/normal/{voucherCode}/paid-amount")
    suspend fun getPaidAmountOfVoucher(
        @Header("Authorization") token:String,
        @Path("voucherCode")voucherCode:String
    ): Response<PaidAmountOfVoucherResponse>

    @GET("api/sales/normal/get-gold-price")
    suspend fun getGoldPrices(
        @Header("Authorization") token:String,
        @Query("product_id[]")productIdList:List<String>
    ): Response<GoldPriceResponse>

    @GET("api/sales/normal/get-voucher-info-kpy")
    suspend fun getVoucherInfoWithKPY(
        @Header("Authorization") token:String,
        @Query("product_id[]")productIdList:List<String>
    ): Response<VoucherInfoWithKPYResponse>
}