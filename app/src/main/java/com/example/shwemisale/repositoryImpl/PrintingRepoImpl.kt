package com.example.shwemisale.repositoryImpl

import com.example.shwemi.util.Resource
import com.example.shwemi.util.parseError
import com.example.shwemi.util.parseErrorWithDataClass
import com.example.shwemisale.data_layers.dto.SimpleError
import com.example.shwemisale.data_layers.dto.printing.PawnCreatePrintDto
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.network.api_services.PrintingService
import com.example.shwemisale.repository.PrintingRepository
import javax.inject.Inject

class PrintingRepoImpl @Inject constructor(
    private val printingService: PrintingService,
    private val localDatabase: LocalDatabase
) : PrintingRepository {
    override suspend fun getSalePrint( saleId: String): Resource<String> {
        return try {
            val response = printingService.getRebuyPrint(
                localDatabase.getAccessToken().orEmpty(),
                saleId
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else {
                val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }


    override suspend fun getPawnPrint(
        pawnVoucherId: String
    ): Resource<String> {
        return try {
            val response = printingService.getPawnCreatePrint(
                localDatabase.getAccessToken().orEmpty(),
                pawnVoucherId
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else {
                val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}