package com.example.shwemisale.repositoryImpl

import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemi.util.parseError
import com.example.shwemi.util.parseErrorWithDataClass
import com.example.shwemisale.data_layers.domain.sample.SampleDomain
import com.example.shwemisale.data_layers.dto.GeneralSaleDto
import com.example.shwemisale.data_layers.dto.SimpleError
import com.example.shwemisale.data_layers.dto.calculation.GoldPriceDto
import com.example.shwemisale.data_layers.dto.customers.asDomain
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    override suspend fun submitWithKPY(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        old_stock_qty: List<MultipartBody.Part>?,
        old_stock_size: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,
        oldStockGoldGemWeightY: List<MultipartBody.Part>?,
        oldStockImpurityWeightY: List<MultipartBody.Part>?,
        oldStockGoldWeightY: List<MultipartBody.Part>?,
        oldStockWastageWeightY: List<MultipartBody.Part>?,
        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,
        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,
        oldStockDGoldWeightY: List<MultipartBody.Part>?,
        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,
        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?
    ): Resource<String> {
        return try {
            val response = normalSaleService.submitWithKPY(
                localDatabase.getAccessToken().orEmpty(),
                productIdList,
                user_id,
                paid_amount,
                reduced_cost,
                old_voucher_paid_amount,
                old_stocks_nameList,
                oldStockImageIds,
                oldStockImageFile,
                oldStockCondition,
                old_stock_qty,
                old_stock_size,
                oldStockGemWeightY,
                oldStockGoldGemWeightY,
                oldStockImpurityWeightY,
                oldStockGoldWeightY,
                oldStockWastageWeightY,
                oldStockRebuyPrice,
                oldStockGQinCarat,
                oldStockMaintenance_cost,
                oldStockGemValue,
                oldStockPTAndClipCost,
                oldStockCalculatedBuyingValue,
                oldStockPriceForPawn,
                oldStockCalculatedForPawn,
                oldStockABuyingPrice,
                oldStockb_voucher_buying_value,
                oldStockc_voucher_buying_price,
                oldStockDGoldWeightY,
                oldStockEPriceFromNewVoucher,
                oldStockFVoucherShownGoldWeightY
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

    override suspend fun submitWithValue(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,

        old_voucher_paid_amount: MultipartBody.Part?,
        old_stocks_nameList: List<MultipartBody.Part>?,
        old_stocks_gem_details_gem_qty: List<MultipartBody.Part>?,
        old_stocks_gem_details_gem_weight_gm_per_unit: List<MultipartBody.Part>?,
        old_stocks_gem_details_gem_weight_ywae_per_unit: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        old_stock_qty: List<MultipartBody.Part>?,
        old_stock_size: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,

        oldStockGoldGemWeightY: List<MultipartBody.Part>?,

        oldStockImpurityWeightY: List<MultipartBody.Part>?,

        oldStockGoldWeightY: List<MultipartBody.Part>?,

        oldStockWastageWeightY: List<MultipartBody.Part>?,

        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,

        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,

        oldStockDGoldWeightY: List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,
    ): Resource<String> {
        return try {
            val response = normalSaleService.submitWithValue(
                localDatabase.getAccessToken().orEmpty(),
                productIdList,
                user_id,
                paid_amount,
                reduced_cost,
                old_voucher_paid_amount,
                old_stocks_nameList,
                old_stocks_gem_details_gem_qty,
                old_stocks_gem_details_gem_weight_gm_per_unit,
                old_stocks_gem_details_gem_weight_ywae_per_unit,
                oldStockImageIds,
                oldStockImageFile,
                oldStockCondition,
                old_stock_qty,
                old_stock_size,
                oldStockGemWeightY,
                oldStockGoldGemWeightY,
                oldStockImpurityWeightY,
                oldStockGoldWeightY,
                oldStockWastageWeightY,
                oldStockRebuyPrice,
                oldStockGQinCarat,
                oldStockMaintenance_cost,
                oldStockGemValue,
                oldStockPTAndClipCost,
                oldStockCalculatedBuyingValue,
                oldStockPriceForPawn,
                oldStockCalculatedForPawn,
                oldStockABuyingPrice,
                oldStockb_voucher_buying_value,
                oldStockc_voucher_buying_price,
                oldStockDGoldWeightY,
                oldStockEPriceFromNewVoucher,
                oldStockFVoucherShownGoldWeightY
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
        is_delivered: String,
        remark: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        itemsGeneralSaleItemId: List<MultipartBody.Part>?,
        itemsQty: List<MultipartBody.Part>?,
        itemsGoldWeightGm: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,
        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        oldStockGoldGemWeightY: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,
        oldStockImpurityWeightY: List<MultipartBody.Part>?,
        oldStockGoldWeightY: List<MultipartBody.Part>?,
        oldStockWastageWeightY: List<MultipartBody.Part>?,
        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,
        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,
        oldStockDGoldWeightY: List<MultipartBody.Part>?,
        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,
        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,
        oldStockSampleListId: List<MultipartBody.Part>?
    ): Resource<String> {
        return try {
            val response = normalSaleService.submitOrderSale(
                localDatabase.getAccessToken().orEmpty(),
                name,
                gold_type_id,
                gold_price,
                total_gold_weight_ywae,
                est_unit_wastage_ywae,
                qty,
                gem_value,
                maintenance_cost,
                date_of_delivery,
                is_delivered,
                remark,
                user_id,
                paid_amount,
                reduced_cost,
                itemsGeneralSaleItemId,
                itemsQty,
                itemsGoldWeightGm,
                itemsWastageYwae,
                itemsMaintenanceCost,
                old_stocks_nameList,
                oldStockImageIds,
                oldStockImageFile,
                oldStockCondition,
                oldStockGoldGemWeightY,
                oldStockGemWeightY,
                oldStockImpurityWeightY,
                oldStockGoldWeightY,
                oldStockWastageWeightY,
                oldStockRebuyPrice,
                oldStockGQinCarat,
                oldStockMaintenance_cost,
                oldStockGemValue,
                oldStockPTAndClipCost,
                oldStockCalculatedBuyingValue,
                oldStockPriceForPawn,
                oldStockCalculatedForPawn,
                oldStockABuyingPrice,
                oldStockb_voucher_buying_value,
                oldStockc_voucher_buying_price,
                oldStockDGoldWeightY,
                oldStockEPriceFromNewVoucher,
                oldStockFVoucherShownGoldWeightY,
                oldStockSampleListId
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

    override suspend fun submitPureGoldSale(
        itemsGoldWeightYwae: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,
        itemsThreadingFees: List<MultipartBody.Part>?,
        itemsType: List<MultipartBody.Part>?,
        gold_price: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        oldStockGoldGemWeightY: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,
        oldStockImpurityWeightY: List<MultipartBody.Part>?,
        oldStockGoldWeightY: List<MultipartBody.Part>?,
        oldStockWastageWeightY: List<MultipartBody.Part>?,
        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,
        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,
        oldStockDGoldWeightY: List<MultipartBody.Part>?,
        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,
        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,
        oldStockSampleListId: List<MultipartBody.Part>?
    ): Resource<String> {
        return try {
            val response = normalSaleService.submitPureGoldSale(
                localDatabase.getAccessToken().orEmpty(),
                itemsGoldWeightYwae,
                itemsWastageYwae,
                itemsMaintenanceCost,
                itemsThreadingFees,
                itemsType,
                gold_price,
                user_id,
                paid_amount,
                reduced_cost,
                old_stocks_nameList,
                oldStockImageIds,
                oldStockImageFile,
                oldStockCondition,
                oldStockGoldGemWeightY,
                oldStockGemWeightY,
                oldStockImpurityWeightY,
                oldStockGoldWeightY,
                oldStockWastageWeightY,
                oldStockRebuyPrice,
                oldStockGQinCarat,
                oldStockMaintenance_cost,
                oldStockGemValue,
                oldStockPTAndClipCost,
                oldStockCalculatedBuyingValue,
                oldStockPriceForPawn,
                oldStockCalculatedForPawn,
                oldStockABuyingPrice,
                oldStockb_voucher_buying_value,
                oldStockc_voucher_buying_price,
                oldStockDGoldWeightY,
                oldStockEPriceFromNewVoucher,
                oldStockFVoucherShownGoldWeightY,
                oldStockSampleListId
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

    override suspend fun submitGeneralSale(
        itemsGeneralSaleItemId: List<MultipartBody.Part>?,
        itemsQty: List<MultipartBody.Part>?,
        itemsGoldWeightYwae: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        oldStockGoldGemWeightY: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,
        oldStockImpurityWeightY: List<MultipartBody.Part>?,
        oldStockGoldWeightY: List<MultipartBody.Part>?,
        oldStockWastageWeightY: List<MultipartBody.Part>?,
        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,
        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,
        oldStockDGoldWeightY: List<MultipartBody.Part>?,
        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,
        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?
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
                old_stocks_nameList,
                oldStockImageIds,
                oldStockImageFile,
                oldStockCondition,
                oldStockGoldGemWeightY,
                oldStockGemWeightY,
                oldStockImpurityWeightY,
                oldStockGoldWeightY,
                oldStockWastageWeightY,
                oldStockRebuyPrice,
                oldStockGQinCarat,
                oldStockMaintenance_cost,
                oldStockGemValue,
                oldStockPTAndClipCost,
                oldStockCalculatedBuyingValue,
                oldStockPriceForPawn,
                oldStockCalculatedForPawn,
                oldStockABuyingPrice,
                oldStockb_voucher_buying_value,
                oldStockc_voucher_buying_price,
                oldStockDGoldWeightY,
                oldStockEPriceFromNewVoucher,
                oldStockFVoucherShownGoldWeightY
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

    override suspend fun getGeneralSalesItems(): Resource<List<GeneralSaleDto>> {
        return try {
            val response = normalSaleService.getGeneralSalesItems(
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

    override suspend fun checkSample(productId: String): Resource<SampleDomain> {
        return try {
            val response = normalSaleService.checkInventorySample(
                localDatabase.getAccessToken().orEmpty(),
                productId
            )

            if (response.isSuccessful && response.body() != null) {
                appDatabase.sampleDao.saveSample(response.body()!!.data.asDomain().asEntity())
                Resource.Success(response.body()!!.data.asDomain())
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
                Resource.Success(response.body()!!.data[0].asDomain())
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

    fun getSamplesFromRoom() =  appDatabase.sampleDao.getSamples().map { it.map { it.asDomain() } }
}