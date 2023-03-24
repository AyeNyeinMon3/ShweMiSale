package com.example.shwemisale.repositoryImpl

import android.util.Log
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemi.util.parseError
import com.example.shwemi.util.parseErrorWithDataClass
import com.example.shwemisale.data_layers.domain.generalSale.GeneralSaleListDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.domain.pureGoldSale.PureGoldListDomain
import com.example.shwemisale.data_layers.domain.sample.SampleDomain
import com.example.shwemisale.data_layers.dto.GeneralSaleDto
import com.example.shwemisale.data_layers.dto.SimpleError
import com.example.shwemisale.data_layers.dto.calculation.GoldPriceDto
import com.example.shwemisale.data_layers.dto.customers.asDomain
import com.example.shwemisale.data_layers.dto.generalSale.asDomain
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.data_layers.dto.goldFromHome.Image
import com.example.shwemisale.data_layers.dto.goldFromHome.asDomain
import com.example.shwemisale.data_layers.dto.product.asDomain
import com.example.shwemisale.data_layers.dto.sample.SampleDto
import com.example.shwemisale.data_layers.dto.sample.asDomain
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithKPYDto
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithValueResponse
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.network.api_services.NormalSaleService
import com.example.shwemisale.repository.NormalSaleRepository
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.asDomain
import com.example.shwemisale.room_database.entity.asEntity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject

class NormalSaleRepositoryImpl @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val normalSaleService: NormalSaleService,
    private val appDatabase: AppDatabase
) : NormalSaleRepository {
    override suspend fun getPaidAmountOfVoucher(
        voucherCode: String
    ): Resource<String> {
        return try {
            val response = normalSaleService.getPaidAmountOfVoucher(
                localDatabase.getAccessToken().orEmpty(),
                voucherCode
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

    override suspend fun createStockFromHomeList(
        a_buying_price: String?,
        b_voucher_buying_value: String?,
        c_voucher_buying_price: String?,
        calculated_buying_value: String?,
        calculated_for_pawn: String?,
        d_gold_weight_ywae: String?,
        e_price_from_new_voucher: String?,
        f_voucher_shown_gold_weight_ywae: String?,
        gem_value: String?,
        gem_weight_details_qty: List<MultipartBody.Part?>?,
        gem_weight_details_gm: List<MultipartBody.Part?>?,
        gem_weight_details_ywae: List<MultipartBody.Part?>?,
        gem_weight_ywae: String?,
        gold_gem_weight_ywae: String?,
        gold_weight_ywae: String?,
        gq_in_carat: String?,
        has_general_expenses: String?,
        imageId: String?,
        imageFile: RequestBody?,
        impurities_weight_ywae: String?,
        maintenance_cost: String?,
        price_for_pawn: String?,
        pt_and_clip_cost: String?,
        qty: String?,
        rebuy_price: String?,
        size: String?,
        stock_condition: String?,
        stock_name: String?,
        type: String?,
        wastage_ywae: String?,
        rebuy_price_vertical_option: String?,
        sessionKey: String?
    ): Resource<String> {
        val session =
            if (sessionKey.isNullOrEmpty()) null else sessionKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return try {
            val response = normalSaleService.createStockFromHome(
                localDatabase.getAccessToken().orEmpty(),
                a_buying_price.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                b_voucher_buying_value.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                c_voucher_buying_price.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                calculated_buying_value.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                calculated_for_pawn.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                d_gold_weight_ywae.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                e_price_from_new_voucher.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                f_voucher_shown_gold_weight_ywae.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gem_value.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gem_weight_details_qty,
                gem_weight_details_gm,
                gem_weight_details_ywae,
                gem_weight_ywae.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gold_gem_weight_ywae.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gold_weight_ywae.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gq_in_carat.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                has_general_expenses.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                imageId.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                imageFile,
                impurities_weight_ywae.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                maintenance_cost.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                price_for_pawn.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                pt_and_clip_cost.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                qty.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                rebuy_price.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                size.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                stock_condition.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                stock_name.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                type.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                wastage_ywae.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                rebuy_price_vertical_option.orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                session
            )

            if (response.isSuccessful && response.body() != null) {
                if (localDatabase.getStockFromHomeSessionKey().isNullOrEmpty()) {
                    localDatabase.saveStockFromHomeSessionKey(response.body()!!.data)
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
        a_buying_price: List<MultipartBody.Part>?,
        b_voucher_buying_value: List<MultipartBody.Part>?,
        c_voucher_buying_price: List<MultipartBody.Part>?,
        calculated_buying_value: List<MultipartBody.Part>?,
        calculated_for_pawn: List<MultipartBody.Part>?,
        d_gold_weight_ywae: List<MultipartBody.Part>?,
        e_price_from_new_voucher: List<MultipartBody.Part>?,
        f_voucher_shown_gold_weight_ywae: List<MultipartBody.Part>?,
        gem_value: List<MultipartBody.Part>?,
        gem_weight_details_qty: List<MultipartBody.Part?>?,
        gem_weight_details_gm: List<MultipartBody.Part?>?,
        gem_weight_details_ywae: List<MultipartBody.Part?>?,
        gem_weight_ywae: List<MultipartBody.Part>?,
        gold_gem_weight_ywae: List<MultipartBody.Part>?,
        gold_weight_ywae: List<MultipartBody.Part>?,
        gq_in_carat: List<MultipartBody.Part>?,
        has_general_expenses: List<MultipartBody.Part>?,
        imageId: List<MultipartBody.Part>?,
        imageFile: List<MultipartBody.Part>?,
        impurities_weight_ywae: List<MultipartBody.Part>?,
        maintenance_cost: List<MultipartBody.Part>?,
        price_for_pawn: List<MultipartBody.Part>?,
        pt_and_clip_cost: List<MultipartBody.Part>?,
        qty: List<MultipartBody.Part>?,
        rebuy_price: List<MultipartBody.Part>?,
        size: List<MultipartBody.Part>?,
        stock_condition: List<MultipartBody.Part>?,
        stock_name: List<MultipartBody.Part>?,
        type: List<MultipartBody.Part>?,
        wastage_ywae: List<MultipartBody.Part>?,
        rebuy_price_vertical_option:  List<MultipartBody.Part>?,
        sessionKey: String?
    ): Resource<String> {
        val session =
            if (sessionKey.isNullOrEmpty()) null else sessionKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return try {
            val response = normalSaleService.updateStockFromHome(
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
                gem_weight_details_qty,
                gem_weight_details_gm,
                gem_weight_details_ywae,
                gem_weight_ywae,
                gold_gem_weight_ywae,
                gold_weight_ywae,
                gq_in_carat,
                has_general_expenses,
                imageId,
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
                session
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
            Log.i("customError", e.message.orEmpty())
            Resource.Error(e.message)
        }
    }

    override suspend fun submitWithKPY(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_stock_session_key: RequestBody,

        ): Resource<String> {
        return try {
            val response = normalSaleService.submitWithKPY(
                localDatabase.getAccessToken().orEmpty(),
                productIdList,
                user_id,
                paid_amount,
                reduced_cost,
                old_stock_session_key
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
            Resource.Error(e.message)
        }
    }

    override suspend fun submitWithValue(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_stock_session_key: RequestBody,

        ): Resource<String> {
        return try {
            val response = normalSaleService.submitWithValue(
                localDatabase.getAccessToken().orEmpty(),
                productIdList,
                user_id,
                paid_amount,
                reduced_cost,
                old_voucher_paid_amount,
                old_stock_session_key
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
        old_stock_session_key: RequestBody,
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
                oldStockSampleListId
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
                localDatabase.removeSessionKey()
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
            Resource.Error(e.message)
        }
    }

    override suspend fun submitGeneralSale(
        itemsGeneralSaleItemId: List<MultipartBody.Part>?,
        itemsQty: List<MultipartBody.Part>?,
        itemsGoldWeightYwae: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stock_session_key: RequestBody,

        ): Resource<String> {
        return try {
            val response = normalSaleService.submitGeneralSale(
                localDatabase.getAccessToken().orEmpty(),
                itemsGeneralSaleItemId,
                itemsQty,
                itemsGoldWeightYwae,
                itemsWastageYwae,
                itemsMaintenanceCost,
                user_id,
                paid_amount,
                reduced_cost,
                old_stock_session_key
                )

            if (response.isSuccessful && response.body() != null) {
                localDatabase.removeSessionKey()
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
                appDatabase.sampleDao.saveSample(response.body()!!.data.asDomain().asEntity())
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

    override suspend fun saveSample(samples: HashMap<String, String>): Resource<SampleDomain> {
        return try {
            val response = normalSaleService.saveSample(
                localDatabase.getAccessToken().orEmpty(),
                samples
            )

            if (response.isSuccessful && response.body() != null) {
                appDatabase.sampleDao.saveSample(response.body()!!.data[0].asDomain().asEntity())
                Resource.Success(response.body()!!.data[0].asDomain())
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
                appDatabase.sampleDao.saveSample(response.body()!!.data.asDomain().asEntity())
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
                if (localDatabase.getSessionKey().isNullOrEmpty()) {
                    localDatabase.saveSessionKey(response.body()!!.data)
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
        return try {
            val response = normalSaleService.createGeneralSaleItem(
                localDatabase.getAccessToken().orEmpty(),
                general_sale_item_id.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                gold_weight_gm.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                maintenance_cost.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                qty.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                wastage_ywae.toRequestBody("multipart/form-data".toMediaTypeOrNull()),

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