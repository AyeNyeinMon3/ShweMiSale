package com.example.shwemisale.repositoryImpl

import com.example.shwemi.util.Resource
import com.example.shwemi.util.parseError
import com.example.shwemi.util.parseErrorWithDataClass
import com.example.shwemisale.data_layers.dto.RemainingAmountDto
import com.example.shwemisale.data_layers.dto.SimpleError
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateDto
import com.example.shwemisale.data_layers.dto.pawn.PawnVoucherScanDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.network.api_services.PawnService
import com.example.shwemisale.repository.PawnRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PawnRepositoryImpl @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val pawnService: PawnService
) : PawnRepository {
    override suspend fun getPawnInterestRate(): Resource<List<PawnInterestRateDto>> {
        return try {
            val response = pawnService.getPawnInterestRate(
                localDatabase.getAccessToken().orEmpty(),
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

    override suspend fun getPawnVoucherScan(
        voucherCode: String
    ): Resource<PawnVoucherScanDto> {
        return try {
            val response = pawnService.getPawnVoucherScan(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode
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

    override suspend fun storePawn(
        user_id: String?,
        total_debt_amount: String?,
        interest_rate: String?,
        warning_period_months: String?,
        interest_free_from: String?,
        interest_free_to: String?,
        is_app_functions_allowed:String?,
        old_session_key:String

    ): Resource<String> {
        return try {
            val response = pawnService.storePawn(
                localDatabase.getAccessToken().orEmpty(),
                user_id.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                total_debt_amount.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                interest_rate.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                warning_period_months.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                interest_free_from.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                interest_free_to.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                is_app_functions_allowed.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                old_session_key.toRequestBody("multipart/form-data".toMediaTypeOrNull())

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

    override suspend fun createPrepaidDebt(
        voucherCode: String,
        prepaid_debt: String,
        reduced_amount: String,
        is_app_functions_allowed:String?,

        ): Resource<String> {
        return try {
            val response = pawnService.createPrepaidDebt(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode, prepaid_debt, reduced_amount,
                is_app_functions_allowed
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

    override suspend fun createPrepaidInterest(
        voucherCode: String,
        number_of_months: String,
        reduced_amount: String,
        is_app_functions_allowed:String?,

        ): Resource<String> {
        return try {
            val response = pawnService.createPrepaidInterest(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode, number_of_months, reduced_amount,is_app_functions_allowed
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

    override suspend fun increaseDebt(
        voucherCode: String,
        increased_debt: String,
        reduced_amount: String,
        is_app_functions_allowed:String?,
        old_session_key:String

    ): Resource<String> {
        return try {
            val response = pawnService.increaseDebt(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode,
                increased_debt,
                reduced_amount,
                is_app_functions_allowed,
                old_session_key
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

    override suspend fun payInterestAndIncreaseDebt(
        voucherCode: String,
        increased_debt: String,
        reduced_amount: String,
        is_app_functions_allowed:String?,
        old_session_key:String

    ): Resource<String> {
        return try {
            val response = pawnService.payInterestAndIncreaseDebt(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode,
                increased_debt,
                reduced_amount,
                is_app_functions_allowed,
                old_session_key
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

    override suspend fun payInterest(
        voucherCode: String,
        reduced_amount: String,
        is_app_functions_allowed:String?,

        ): Resource<String> {
        return try {
            val response = pawnService.payInterest(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode, reduced_amount,is_app_functions_allowed
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

    override suspend fun payInterestAndSettleDebt(
        voucherCode: String,
        reduced_amount: String,
        debt: String,
        is_app_functions_allowed:String?,

        ): Resource<String> {
        return try {
            val response = pawnService.payInterestAndSettleDebt(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode, reduced_amount, debt,is_app_functions_allowed
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

    override suspend fun payInterestAndReturnStock(
        voucherCode: String,
        reduced_amount: String,
        debt: String,
        is_app_functions_allowed:String?,
        old_stock_id: List<String>
    ): Resource<String> {
        return try {
            val response = pawnService.payInterestAndReturnStock(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode, reduced_amount, debt, old_stock_id,is_app_functions_allowed
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

    override suspend fun settle(
        voucherCode: String,
        reduced_amount: String,
        is_app_functions_allowed:String?,

        ): Resource<String> {
        return try {
            val response = pawnService.settle(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode, reduced_amount,is_app_functions_allowed
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

    override suspend fun sellOldStock(
        voucherCode: String,
        reduced_amount: String,
        is_app_functions_allowed:String?,
        old_session_key:String

    ): Resource<String> {
        return try {
            val response = pawnService.sellOldStock(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode,
                reduced_amount,
                is_app_functions_allowed,
                old_session_key

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

    override suspend fun payBalance(sale_id: String, paid_amount: String?): Resource<String> {
        return try {
            val response = pawnService.payBalance(
                localDatabase.getAccessToken().orEmpty(),
                sale_id, paid_amount

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

    override suspend fun getRemainingAmount(
        saleCode: String?
    ): Resource<RemainingAmountDto> {
        return try {
            val response = pawnService.getRemainingAmount(
                localDatabase.getAccessToken().orEmpty(),
                saleCode

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