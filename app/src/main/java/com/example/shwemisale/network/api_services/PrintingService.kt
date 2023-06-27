package com.example.shwemisale.network.api_services

import com.example.shwemisale.data_layers.dto.printing.PawnCreatePrintResponse
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintResponse
import com.example.shwemisale.data_layers.dto.product.SessionKeyResponse
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

    @GET("api/pawn/{pawnCreateId}/print")
    suspend fun getPawnCreatePrint(
        @Header("Authorization") token: String,
        @Path("pawnCreateId") pawnCreateId: String
    ): Response<PawnCreatePrintResponse>
}