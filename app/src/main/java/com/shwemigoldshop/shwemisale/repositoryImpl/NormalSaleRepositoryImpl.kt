package com.shwemigoldshop.shwemisale.repositoryImpl

import android.util.Log
import androidx.lifecycle.map
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.parseError
import com.shwemigoldshop.shwemisale.util.parseErrorWithDataClass
import com.shwemigoldshop.shwemisale.data_layers.domain.generalSale.GeneralSaleListDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.PaidAmountOfVoucherDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.pureGoldSale.PureGoldListDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.sample.SampleDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.GeneralSaleDto
import com.shwemigoldshop.shwemisale.data_layers.dto.SimpleError
import com.shwemigoldshop.shwemisale.data_layers.dto.asDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldPriceDto
import com.shwemigoldshop.shwemisale.data_layers.dto.generalSale.asDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.goldFromHome.asDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.product.asDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.sample.asDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.voucher.VoucherInfoWithKPYDto
import com.shwemigoldshop.shwemisale.data_layers.dto.voucher.VoucherInfoWithValueResponse
import com.shwemigoldshop.shwemisale.data_layers.dto.voucher.asDomain
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.network.api_services.NormalSaleService
import com.shwemigoldshop.shwemisale.repository.NormalSaleRepository
import com.shwemigoldshop.shwemisale.room_database.AppDatabase
import com.shwemigoldshop.shwemisale.room_database.entity.asDomain
import com.shwemigoldshop.shwemisale.room_database.entity.asEntity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class NormalSaleRepositoryImpl @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val normalSaleService: NormalSaleService,
    private val appDatabase: AppDatabase
) : NormalSaleRepository {
    override suspend fun getPaidAmountOfVoucher(
        voucherCode: String
    ): Resource<PaidAmountOfVoucherDomain> {
        return try {
            val response = normalSaleService.getPaidAmountOfVoucher(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode,
                localDatabase.getStockFromHomeSessionKey()
                    .let { if (it.isNullOrEmpty()) null else it }
            )

            if (response.isSuccessful && response.body() != null) {
                localDatabase.saveStockFromHomeSessionKey(response.body()!!.data.old_stock_session_key.orEmpty())
                Resource.Success(response.body()!!.data.asDomain())
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun getGoldPrices(
        productIdList: List<String>
    ): Resource<GoldPriceDto> {
        return try {
            val response = normalSaleService.getGoldPrices(
                localDatabase.getAccessToken().orEmpty(),
                productIdList
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun getVoucherInfoWithKPY(
        productIdList: List<String>
    ): Resource<VoucherInfoWithKPYDto> {
        return try {
            val response = normalSaleService.getVoucherInfoWithKPY(
                localDatabase.getAccessToken().orEmpty(),
                productIdList
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun getVoucherInfoWithValue(
        productIdList: List<String>
    ): Resource<VoucherInfoWithValueResponse> {
        return try {
            val response = normalSaleService.getVoucherInfoWithValue(
                localDatabase.getAccessToken().orEmpty(),
                productIdList
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun getStockFromHomeList(sessionKey: String?): Resource<List<StockFromHomeDomain>> {
        return try {
            val response = normalSaleService.getStockFromHomeList(
                localDatabase.getAccessToken().orEmpty(),
                sessionKey
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.map { it.asDomain() })
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun getStockFromHomeForPawn(
        pawnVoucherCode: String?,
        is_pawned_sale:String?
    ): Resource<List<StockFromHomeDomain>> {
        return try {
            val response = normalSaleService.getStockFromHomeForPawn(
                localDatabase.getAccessToken().orEmpty(),
                pawnVoucherCode.orEmpty(),
                is_pawned_sale.orEmpty()
            )

            if (response.isSuccessful && response.body() != null) {
                localDatabase.savePawnOldStockSessionKey(response.body()!!.data.session_key)
                Resource.Success(response.body()!!.data.old_stocks.map { it.asDomain() })
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun createStockFromHomeList(
        a_buying_price: MultipartBody.Part?,
        b_voucher_buying_value: MultipartBody.Part?,
        c_voucher_buying_price: MultipartBody.Part?,
        calculated_buying_value: MultipartBody.Part?,
        calculated_for_pawn: MultipartBody.Part?,
        d_gold_weight_ywae: MultipartBody.Part?,
        e_price_from_new_voucher: MultipartBody.Part?,
        f_voucher_shown_gold_weight_ywae: MultipartBody.Part?,
        gem_value: MultipartBody.Part?,
        gem_weight_details_session_key: MultipartBody.Part?,
        gem_weight_ywae: MultipartBody.Part?,
        gold_gem_weight_ywae: MultipartBody.Part?,
        gold_weight_ywae: MultipartBody.Part?,
        gq_in_carat: MultipartBody.Part?,
        has_general_expenses: MultipartBody.Part?,
        imageFile: MultipartBody.Part?,
        imageId: MultipartBody.Part?,
        impurities_weight_ywae: MultipartBody.Part?,
        maintenance_cost: MultipartBody.Part?,
        price_for_pawn: MultipartBody.Part?,
        pt_and_clip_cost: MultipartBody.Part?,
        qty: MultipartBody.Part?,
        rebuy_price: MultipartBody.Part?,
        size: MultipartBody.Part?,
        stock_condition: MultipartBody.Part?,
        stock_name: MultipartBody.Part?,
        type: MultipartBody.Part?,
        wastage_ywae: MultipartBody.Part?,
        rebuy_price_vertical_option: MultipartBody.Part?,
        productIdList: List<MultipartBody.Part?>?,
        isEditable: MultipartBody.Part?,
        isChecked: MultipartBody.Part?,
        sessionKey: String?,
        isPawn: Boolean
    ): Resource<String> {
        val session =
            if (sessionKey.isNullOrEmpty()) null else sessionKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return try {
            val response = normalSaleService.createStockFromHome(
                localDatabase.getAccessToken().orEmpty(),
                a_buying_price,
                b_voucher_buying_value,
                c_voucher_buying_price,
                calculated_buying_value,
                calculated_for_pawn,
                d_gold_weight_ywae,
                e_price_from_new_voucher,
                f_voucher_shown_gold_weight_ywae,
                gem_value,
                gem_weight_details_session_key,
                gem_weight_ywae,
                gold_gem_weight_ywae,
                gold_weight_ywae,
                gq_in_carat,
                has_general_expenses,
                imageFile,
                imageId,
                impurities_weight_ywae,
                maintenance_cost,
                price_for_pawn,
                pt_and_clip_cost,
                qty,
                rebuy_price,
                size,
                stock_condition,
                stock_name,
                type,
                wastage_ywae,
                rebuy_price_vertical_option,
                productIdList,
                isEditable,
                isChecked,
                session
            )

            if (response.isSuccessful && response.body() != null) {
                //saving for pawn or sale
                if (localDatabase.getStockFromHomeSessionKey().isNullOrEmpty() && !isPawn) {
                    localDatabase.saveStockFromHomeSessionKey(response.body()!!.data)
                } else if (localDatabase.getPawnOldStockSessionKey().isNullOrEmpty() && isPawn) {
                    localDatabase.savePawnOldStockSessionKey(response.body()!!.data)
                }
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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
            Log.i("customError", e.message.orEmpty())
            Resource.Error(e.message)
        }
    }


    override suspend fun updateStockFromHomeList(
        id: MultipartBody.Part?,
        a_buying_price: MultipartBody.Part?,
        b_voucher_buying_value: MultipartBody.Part?,
        c_voucher_buying_price: MultipartBody.Part?,
        calculated_buying_value: MultipartBody.Part?,
        calculated_for_pawn: MultipartBody.Part?,
        d_gold_weight_ywae: MultipartBody.Part?,
        e_price_from_new_voucher: MultipartBody.Part?,
        f_voucher_shown_gold_weight_ywae: MultipartBody.Part?,
        gem_value: MultipartBody.Part?,
        gem_weight_details_session_key: MultipartBody.Part?,
        gem_weight_ywae: MultipartBody.Part?,
        gold_gem_weight_ywae: MultipartBody.Part?,
        gold_weight_ywae: MultipartBody.Part?,
        gq_in_carat: MultipartBody.Part?,
        has_general_expenses: MultipartBody.Part?,
        imageFile: MultipartBody.Part?,
        impurities_weight_ywae: MultipartBody.Part?,
        maintenance_cost: MultipartBody.Part?,
        price_for_pawn: MultipartBody.Part?,
        pt_and_clip_cost: MultipartBody.Part?,
        qty: MultipartBody.Part?,
        rebuy_price: MultipartBody.Part?,
        size: MultipartBody.Part?,
        stock_condition: MultipartBody.Part?,
        stock_name: MultipartBody.Part?,
        type: MultipartBody.Part?,
        wastage_ywae: MultipartBody.Part?,
        rebuy_price_vertical_option: MultipartBody.Part?,
        productIdList: List<MultipartBody.Part?>?,
        isEditable: MultipartBody.Part?,
        isChecked: MultipartBody.Part?,
        sessionKey: String?,
        isPawn: Boolean
    ): Resource<String> {

        return try {
            val response = normalSaleService.updateStockFromHome(
                localDatabase.getAccessToken().orEmpty(),
                id,
                a_buying_price,
                b_voucher_buying_value,
                c_voucher_buying_price,
                calculated_buying_value,
                calculated_for_pawn,
                d_gold_weight_ywae,
                e_price_from_new_voucher,
                f_voucher_shown_gold_weight_ywae,
                gem_value,
                gem_weight_details_session_key,
                gem_weight_ywae,
                gold_gem_weight_ywae,
                gold_weight_ywae,
                gq_in_carat,
                has_general_expenses,
                imageFile,
                impurities_weight_ywae,
                maintenance_cost,
                price_for_pawn,
                pt_and_clip_cost,
                qty,
                rebuy_price,
                size,
                stock_condition,
                stock_name,
                type,
                wastage_ywae,
                rebuy_price_vertical_option,
                productIdList,
                isEditable,
                isChecked,

                )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.response.message)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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
            Log.i("customError", e.message.orEmpty())
            Resource.Error(e.message)
        }
    }

    override suspend fun updateEValue(eValue: String, sessionKey: String?): Resource<String> {
        return try {
            val response = normalSaleService.updateEvalueOldStocks(
                localDatabase.getAccessToken().orEmpty(),
                sessionKey,
                eValue
            )

            if (response.isSuccessful && response.body() != null) {
                localDatabase.saveEValue(eValue)
                Resource.Success(response.body()!!.response.message)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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
            Log.i("customError", e.message.orEmpty())
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteOldStock(oldStockId: String): Resource<String> {

        return try {
            val response = normalSaleService.deleteOldStock(
                localDatabase.getAccessToken().orEmpty(),
                oldStockId
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.response.message)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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
            Log.i("customError", e.message.orEmpty())
            Resource.Error(e.message)
        }
    }

    override suspend fun submitWithKPY(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,
        redeem_point: RequestBody?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_voucher_code: RequestBody?,
        old_stock_session_key: RequestBody?,
        old_stock_calc_type: RequestBody,
        ): Resource<String> {
        return try {
            val response = normalSaleService.submitWithKPY(
                localDatabase.getAccessToken().orEmpty(),
                productIdList,
                user_id,
                paid_amount,
                reduced_cost,
                redeem_point,
                old_voucher_code,
                old_voucher_paid_amount,
                old_stock_session_key,
                old_stock_calc_type
            )

            if (response.isSuccessful && response.body() != null) {
                localDatabase.removeStockFromHomeSessionKey()
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun submitWithValue(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,
        redeem_point: RequestBody?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_voucher_code: RequestBody?,
        old_stock_session_key: RequestBody,

        ): Resource<String> {
        return try {
            val response = normalSaleService.submitWithValue(
                localDatabase.getAccessToken().orEmpty(),
                productIdList,
                user_id,
                paid_amount,
                reduced_cost,
                redeem_point,
                old_voucher_code,
                old_voucher_paid_amount,
                old_stock_session_key
            )

            if (response.isSuccessful && response.body() != null) {
                localDatabase.removeStockFromHomeSessionKey()
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun submitOrderSale(
        name: String,
        gold_type_id: String,
        gold_price: String,
        total_gold_weight_ywae: String,
        est_unit_wastage_ywae: String,
        qty: String,
        gem_value: String,
        maintenance_cost: String,
        date_of_delivery: String,
        remark: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stock_session_key: RequestBody?,
        old_stock_calc_type: RequestBody,
        oldStockSampleListId: List<MultipartBody.Part>?
    ): Resource<String> {
        return try {
            val response = normalSaleService.submitOrderSale(
                localDatabase.getAccessToken().orEmpty(),
                name.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gold_type_id.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gold_price.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                total_gold_weight_ywae.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                est_unit_wastage_ywae.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                qty.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gem_value.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                maintenance_cost.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                date_of_delivery.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                remark.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                user_id.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                paid_amount.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                reduced_cost.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                old_stock_session_key,
                old_stock_calc_type,
                oldStockSampleListId
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun submitPureGoldSale(
        sessionKey: String,
        gold_price: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stock_session_key: RequestBody,
    ): Resource<String> {
        return try {
            val response = normalSaleService.submitPureGoldSale(
                localDatabase.getAccessToken().orEmpty(),
                sessionKey.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gold_price.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                user_id.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                paid_amount.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                reduced_cost.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                old_stock_session_key
            )
            if (response.isSuccessful && response.body() != null) {
                localDatabase.removeGeneralSaleSessionKey()
                localDatabase.removeStockFromHomeSessionKey()
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun submitGeneralSale(
        sessionKey: RequestBody,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stock_session_key: RequestBody,

        ): Resource<String> {
        return try {
            val response = normalSaleService.submitGeneralSale(
                localDatabase.getAccessToken().orEmpty(),
                sessionKey,
                user_id.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                paid_amount.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                reduced_cost.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                old_stock_session_key
            )

            if (response.isSuccessful && response.body() != null) {
                localDatabase.removeGeneralSaleSessionKey()
                localDatabase.removeStockFromHomeSessionKey()
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun getGeneralSalesItems(): Resource<List<GeneralSaleDto>> {
        return try {
            val response = normalSaleService.getGeneralSalesItems(
                localDatabase.getAccessToken().orEmpty(),
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun checkSample(productId: String): Resource<SampleDomain> {
        return try {
            val response = normalSaleService.checkInventorySample(
                localDatabase.getAccessToken().orEmpty(),
                productId
            )

            if (response.isSuccessful && response.body() != null) {
                appDatabase.sampleDao.saveSample(response.body()!!.data.asDomain(true).asEntity())
                Resource.Success(response.body()!!.data.asDomain(true))
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun saveSample(samples: HashMap<String, String>): Resource<SampleDomain> {
        return try {
            val response = normalSaleService.saveSample(
                localDatabase.getAccessToken().orEmpty(),
                samples
            )

            if (response.isSuccessful && response.body() != null) {
                appDatabase.sampleDao.deleteSamplesWithProductId(response.body()!!.data[0].product_id.orEmpty())
                appDatabase.sampleDao.saveSample(
                    response.body()!!.data[0].asDomain(true).asEntity()
                )
                Resource.Success(response.body()!!.data[0].asDomain(true))
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun saveOutsideSample(
        name: RequestBody?,
        weight: RequestBody?,
        specification: RequestBody?,
        image: MultipartBody.Part
    ): Resource<SampleDomain> {
        return try {
            val response = normalSaleService.saveOutsideSample(
                localDatabase.getAccessToken().orEmpty(),
                name,
                weight,
                specification,
                image
            )

            if (response.isSuccessful && response.body() != null) {
                appDatabase.sampleDao.saveSample(response.body()!!.data.asDomain(false).asEntity())
                Resource.Success(response.body()!!.data.asDomain(false))
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun getPureGoldItems(sessionKey: String): Resource<List<PureGoldListDomain>> {
        return try {
            val response = normalSaleService.getPureGoldItems(
                localDatabase.getAccessToken().orEmpty(),
                sessionKey
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.map { it.asDomain() })
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun updatePureGoldItems(
        gold_weight_ywae: List<MultipartBody.Part>?,
        maintenance_cost: List<MultipartBody.Part>?,
        threading_fees: List<MultipartBody.Part>?,
        type: List<MultipartBody.Part>?,
        wastage_ywae: List<MultipartBody.Part>?,
        sessionKey: String?
    ): Resource<String> {
        return try {
            val response = normalSaleService.updatePureGoldSaleItem(
                localDatabase.getAccessToken().orEmpty(),
                gold_weight_ywae,
                maintenance_cost,
                threading_fees,
                type,
                wastage_ywae,
                sessionKey.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun createPureGoldItems(
        gold_weight_ywae: String,
        maintenance_cost: String,
        threading_fees: String,
        type: String,
        wastage_ywae: String,
        sessionKey: String?
    ): Resource<String> {
        val session =
            if (sessionKey.isNullOrEmpty()) null else sessionKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return try {
            val response = normalSaleService.createPureGoldSaleItem(
                localDatabase.getAccessToken().orEmpty(),
                gold_weight_ywae.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                maintenance_cost.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                threading_fees.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                type.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                wastage_ywae.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                session
            )

            if (response.isSuccessful && response.body() != null) {
                if (localDatabase.getPureGoldSessionKey().isNullOrEmpty()) {
                    localDatabase.savePureGoldSessionKey(response.body()!!.data)
                }
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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
            Log.i("customError", e.message.orEmpty())
            Resource.Error(e.message)
        }
    }

    override suspend fun getGeneralSaleItems(sessionKey: String): Resource<List<GeneralSaleListDomain>> {
        return try {
            val response = normalSaleService.getGeneralSaleItems(
                localDatabase.getAccessToken().orEmpty(),
                sessionKey
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.map { it.asDomain() })
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun updateGeneralSaleItems(
        general_sale_item_id: List<MultipartBody.Part>?,
        gold_weight_gm: List<MultipartBody.Part>?,
        maintenance_cost: List<MultipartBody.Part>?,
        qty: List<MultipartBody.Part>?,
        wastage_ywae: List<MultipartBody.Part>?,
        sessionKey: String?
    ): Resource<String> {
        return try {
            val response = normalSaleService.updateGeneralSaleItem(
                localDatabase.getAccessToken().orEmpty(),
                general_sale_item_id,
                gold_weight_gm,
                maintenance_cost,
                qty,
                wastage_ywae,
                sessionKey.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun createGeneralSaleItems(
        general_sale_item_id: String,
        gold_weight_gm: String,
        maintenance_cost: String,
        qty: String,
        wastage_ywae: String
    ): Resource<String> {
        var sessionKey = localDatabase.getGeneralSaleSessionKey().orEmpty()
        val session =
            if (sessionKey.isEmpty()) null else sessionKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return try {
            val response = normalSaleService.createGeneralSaleItem(
                localDatabase.getAccessToken().orEmpty(),
                general_sale_item_id = general_sale_item_id.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gold_weight_gm = gold_weight_gm.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                maintenance_cost = maintenance_cost.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                qty = qty.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                wastage_ywae = wastage_ywae.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                session_key = session
            )

            if (response.isSuccessful && response.body() != null) {
                if (localDatabase.getGeneralSaleSessionKey().isNullOrEmpty()) {
                    localDatabase.saveGeneralSaleSessionKey(response.body()!!.data)
                }
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun getUserRedeemPoints(userId: String): Resource<String> {
        return try {
            val response = normalSaleService.getUserRedeemPoints(
                localDatabase.getAccessToken().orEmpty(),
                userId
            )

            if (response.isSuccessful && response.body() != null) {
//                if (localDatabase.getGeneralSaleSessionKey().isNullOrEmpty()) {
//                    localDatabase.saveGeneralSaleSessionKey(response.body()!!.data)
//                }
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun getRedeemMoney(redeemAmount: String): Resource<String> {
        return try {
            val response = normalSaleService.getRedeemAmount(
                localDatabase.getAccessToken().orEmpty(),
                redeemAmount
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    override suspend fun buyOldStock(): Resource<String> {
        return try {
            val response = normalSaleService.buyOldStocks(
                localDatabase.getAccessToken().orEmpty(),
                localDatabase.getAccessCustomerId().orEmpty(),
                localDatabase.getStockFromHomeSessionKey().orEmpty()
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
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

    fun getSamplesFromRoom() = appDatabase.sampleDao.getSamples().map { it.map { it.asDomain() } }
}