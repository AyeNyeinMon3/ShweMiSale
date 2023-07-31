package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.dto.SimpleResponseWithDataString
import com.example.shwemisale.data_layers.dto.printing.PawnCreatePrintResponse
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface PrintingService {
    @GET("api/rebuys/{rebuyId}/print")
    suspend fun getRebuyPrint(
        @Header("Authorization") token: String,
        @Path("rebuyId") rebuyId: String
    ): Response<RebuyPrintResponse>

    @GET("api/pawn/{pawnItemSaleId}/print")
    suspend fun getPawnItemSalePrint(
        @Header("Authorization") token: String,
        @Path("pawnItemSaleId") pawnItemSaleId: String
    ): Response<PawnCreatePrintResponse>

    @GET("api/sales/{saleId}/voucher-download")
    suspend fun getSalePrint(
        @Header("Authorization") token: String,
        @Path("saleId") saleId: String
    ): Response<SimpleResponseWithDataString>

    @GET("api/pawns/{pawnVoucherId}/voucher-download")
    suspend fun getPawnPdfPrint(
        @Header("Authorization") token: String,
        @Path("pawnVoucherId") pawnVoucherId: String
    ): Response<SimpleResponseWithDataString>
}