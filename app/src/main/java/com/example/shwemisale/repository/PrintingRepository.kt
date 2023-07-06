package com.example.shwemisale.repository

import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.dto.printing.PawnCreatePrintDto
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintDto

interface PrintingRepository {
    suspend fun getSalePrint(
        saleId: String
    ): Resource<String>

    suspend fun getPawnPrint(
        pawnVoucherId: String
    ): Resource<String>
}