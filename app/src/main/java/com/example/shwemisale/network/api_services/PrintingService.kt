package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.printing.PawnCreatePrintResponse
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintResponse
import com.example.shwemisale.data_layers.dto.product.SessionKeyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface PrintingService {
    @GET("api/sales/{saleId}/voucher-download")
    suspend fun getRebuyPrint(
        @Header("Authorization") token: String,
        @Path("saleId") saleId: String
    ): Response<SimpleResponse>

    @GET("api/pawns/{pawnVoucherId}/voucher-download")
    suspend fun getPawnCreatePrint(
        @Header("Authorization") token: String,
        @Path("pawnVoucherId") pawnVoucherId: String
    ): Response<SimpleResponse>
}