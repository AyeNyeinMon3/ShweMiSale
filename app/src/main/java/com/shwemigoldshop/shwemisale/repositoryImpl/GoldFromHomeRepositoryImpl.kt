package com.shwemigoldshop.shwemisale.repositoryImpl

import android.util.Log
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.parseError
import com.shwemigoldshop.shwemisale.util.parseErrorWithDataClass
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockWeightByVoucherDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.SimpleError
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.shwemigoldshop.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import com.shwemigoldshop.shwemisale.data_layers.dto.goldFromHome.asDomain
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.network.api_services.CalculationService
import com.shwemigoldshop.shwemisale.network.api_services.GoldFromHomeService
import com.shwemigoldshop.shwemisale.repository.GoldFromHomeRepository
import com.shwemigoldshop.shwemisale.room_database.AppDatabase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class GoldFromHomeRepositoryImpl @Inject constructor(
    private val goldFromHomeService: GoldFromHomeService,
    private val calculationService: CalculationService,
    private val localDatabase: LocalDatabase,
    private val appDatabase: AppDatabase,
) : GoldFromHomeRepository {
    override suspend fun getStockWeightByVoucher(voucherCode: String): Resource<List<StockWeightByVoucherDomain>> {
        return try {
            val response = goldFromHomeService.getStockWeightByVoucher(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.map { it.asDomain() })
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
            Log.i("errorCustom", e.message, e)
            Resource.Error(e.message)
        }
    }

    override suspend fun getStockInfoByVoucher(
        voucherCode: String,
        productIdList: List<String>,
        goldPrice18KId:String
    ): Resource<List<StockFromHomeDomain>> {
        return try {
            val response = goldFromHomeService.getStockInfoByVoucher(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode,
                productIdList
            )

            if (response.isSuccessful && response.body() != null) {
//                appDatabase.stockFromHomeInfoDao.saveStockFromHomeInfoList(response.body()!!.data.map { it.asDomain() }.map { it.asEntity() })
                Resource.Success(response.body()!!.data.map { it.asDomain(localDatabase.getEValue().orEmpty(),goldPrice18KId) })
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

    override suspend fun getRebuyPrice(
        horizontal_option_name: String,
        vertical_option_name: String,
        size: String
    ): Resource<RebuyPriceDto> {
        return try {
            val response = goldFromHomeService.getReBuyPrice(
                localDatabase.getAccessToken().orEmpty(),
                horizontal_option_name, vertical_option_name, size
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

    override suspend fun getRebuyItem(size: String): Resource<List<RebuyItemDto>> {
        return try {
            val response = goldFromHomeService.getRebuyItems(
                localDatabase.getAccessToken().orEmpty(), size
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

    override suspend fun getGoldType(goldTypeId: String?): Resource<List<GoldTypePriceDto>> {
        return try {
            val response = calculationService.getGoldTypePrice(
                localDatabase.getAccessToken().orEmpty(),
                goldTypeId
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

    override suspend fun getPawnDiffValue(): Resource<String> {
        return try {
            val response = goldFromHomeService.getPawnDiffValue(
                localDatabase.getAccessToken().orEmpty()
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

    override suspend fun createGemWeightDetail(
        qty: RequestBody,
        weightGmPerUnit: RequestBody,
        weightYwaePerUnit: RequestBody
    ): Resource<String> {
        return try {
            val session =
                if (localDatabase.getGemWeightDetailSessionKey()
                        .isNullOrEmpty()
                ) null else localDatabase.getGemWeightDetailSessionKey()!!
                    .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val response = goldFromHomeService.createGemWeightDetail(
                localDatabase.getAccessToken().orEmpty(),
                qty, weightGmPerUnit, weightYwaePerUnit, session
            )

            if (response.isSuccessful && response.body() != null) {
                if (localDatabase.getGemWeightDetailSessionKey().isNullOrEmpty()) {
                    localDatabase.saveGemWeightDetailSessionKey(response.body()!!.data.orEmpty())
                }
                Resource.Success(response.body()!!.response.message)
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

    override suspend fun updateGemWeightDetail(
        id: RequestBody,
        qty: RequestBody,
        weightGmPerUnit: RequestBody,
        weightYwaePerUnit: RequestBody
    ): Resource<String> {
        return try {
            val response = goldFromHomeService.updateGemWeightDetail(
                localDatabase.getAccessToken().orEmpty(),
                id,qty, weightGmPerUnit, weightYwaePerUnit
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.response.message)
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

    override suspend fun getGemWeightDetail(sessionKey: String): Resource<List<GemWeightDetailDomain>> {
        return try {
            val response = goldFromHomeService.getGemWeightDetail(
                localDatabase.getAccessToken().orEmpty(),
                sessionKey
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.map { it.asDomain() })
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

    override suspend fun deleteGemWeightDetail(id: String): Resource<String> {
        return try {
            val response = goldFromHomeService.deleteGemWeightDetail(
                localDatabase.getAccessToken().orEmpty(),
                id
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.response.message)
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