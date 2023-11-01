package com.shwemigoldshop.shwemisale.repository

import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.dto.GeneralSalePrintDto
import com.shwemigoldshop.shwemisale.data_layers.dto.printing.PawnCreatePrintDto
import com.shwemigoldshop.shwemisale.data_layers.dto.printing.RebuyPrintDto

interface PrintingRepository {
    suspend fun getRebuyPrint(
        rebuyId: String
    ): Resource<RebuyPrintDto>

    suspend fun getPawnItemSalePrint(
        pawnId: String
    ): Resource<PawnCreatePrintDto>

    suspend fun getSalePrint(
        saleId: String
    ): Resource<String>

    suspend fun getPawnPrint(
        pawnVoucherId: String
    ): Resource<String>

    suspend fun getGeneralSalePrint(
        generalSaleId:String
    ): Resource<GeneralSalePrintDto>
}