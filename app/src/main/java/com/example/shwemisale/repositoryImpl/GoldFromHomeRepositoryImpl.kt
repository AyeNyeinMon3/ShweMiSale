package com.example.shwemisale.repositoryImpl

import android.util.Log
import com.example.shwemi.util.Resource
import com.example.shwemi.util.parseError
import com.example.shwemi.util.parseErrorWithDataClass
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeInfoDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockWeightByVoucherDomain
import com.example.shwemisale.data_layers.dto.SimpleError
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.StockFromHomeInfoDto
import com.example.shwemisale.data_layers.dto.goldFromHome.asDomain
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.network.api_services.CalculationService
import com.example.shwemisale.network.api_services.GoldFromHomeService
import com.example.shwemisale.repository.GoldFromHomeRepository
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.dao.StockFromHomeInfoDao
import com.example.shwemisale.room_database.entity.asEntity
import javax.inject.Inject

class GoldFromHomeRepositoryImpl @Inject constructor(
    private val goldFromHomeService: GoldFromHomeService,
    private val calculationService: CalculationService,
    private val localDatabase: LocalDatabase,
    private val appDatabase: AppDatabase,
): GoldFromHomeRepository {
    override suspend fun getStockWeightByVoucher(voucherCode: String): Resource<List<StockWeightByVoucherDomain>> {
        return try {
            val response = goldFromHomeService.getStockWeightByVoucher(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.map{it.asDomain()})
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
            Log.i("errorCustom",e.message,e)
            Resource.Error(e.message)
        }
    }

    override suspend fun getStockInfoByVoucher(voucherCode: String,productIdList:List<String>): Resource<List<StockFromHomeInfoDomain>> {
        return try {
            val response = goldFromHomeService.getStockInfoByVoucher(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode,
                productIdList
            )

            if (response.isSuccessful && response.body() != null) {
                appDatabase.stockFromHomeInfoDao.saveStockFromHomeInfoList(response.body()!!.data.map { it.asDomain() }.map { it.asEntity() })
                Resource.Success(response.body()!!.data.map{it.asDomain()})
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
                localDatabase.getAccessToken().orEmpty(),size
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

    override suspend fun getGoldType(): Resource<List<GoldTypePriceDto>> {
        return try {
            val response = calculationService.getGoldTypePrice(
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
}