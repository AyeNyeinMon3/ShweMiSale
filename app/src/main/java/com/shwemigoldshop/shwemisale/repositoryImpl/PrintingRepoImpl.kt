package com.shwemigoldshop.shwemisale.repositoryImpl

import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.parseError
import com.shwemigoldshop.shwemisale.util.parseErrorWithDataClass
import com.shwemigoldshop.shwemisale.data_layers.dto.GeneralSalePrintDto
import com.shwemigoldshop.shwemisale.data_layers.dto.SimpleError
import com.shwemigoldshop.shwemisale.data_layers.dto.printing.PawnCreatePrintDto
import com.shwemigoldshop.shwemisale.data_layers.dto.printing.RebuyPrintDto
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.network.api_services.PrintingService
import com.shwemigoldshop.shwemisale.repository.PrintingRepository
import javax.inject.Inject

class PrintingRepoImpl @Inject constructor(
    private val printingService: PrintingService,
    private val localDatabase: LocalDatabase
) : PrintingRepository {
    override suspend fun getRebuyPrint(rebuyId: String): Resource<RebuyPrintDto> {
        return try {
            val response = printingService.getRebuyPrint(
                localDatabase.getAccessToken().orEmpty(),
                rebuyId
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


    override suspend fun getPawnItemSalePrint(
        pawnId: String
    ): Resource<PawnCreatePrintDto> {
        return try {
            val response = printingService.getPawnItemSalePrint(
                localDatabase.getAccessToken().orEmpty(),
                pawnId
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

    override suspend fun getSalePrint(saleId: String): Resource<String> {
        return try {
            val response = printingService.getSalePrint(
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

    override suspend fun getPawnPrint(pawnVoucherId: String): Resource<String> {
        return try {
            val response = printingService.getPawnPdfPrint(
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

    override suspend fun getGeneralSalePrint(generalSaleId: String): Resource<GeneralSalePrintDto> {
        return try {
            val response = printingService.getGeneralSalePrint(
                localDatabase.getAccessToken().orEmpty(),
                generalSaleId
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