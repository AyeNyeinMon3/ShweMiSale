package com.example.shwemisale.screen.sellModule.openVoucher.withKPY

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.dto.calculation.GoldPriceDto
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo
import com.example.shwemisale.room_database.entity.asUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Part
import javax.inject.Inject

@HiltViewModel
class WithKPYViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val appDatabase: AppDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase
) : ViewModel() {
    var goldPrice = ""
    private val _getGoldPriceLiveData = MutableLiveData<Resource<GoldPriceDto>>()
    val getGoldPriceLiveData: LiveData<Resource<GoldPriceDto>>
        get() = _getGoldPriceLiveData

    fun getGoldPrice(productIdList:List<String>){
        viewModelScope.launch {
            _getGoldPriceLiveData.value = Resource.Loading()
            _getGoldPriceLiveData.value = normalSaleRepositoryImpl.getGoldPrices(productIdList)
        }
    }

    fun getStockFromHomeFinalInfo():StockFromHomeFinalInfo{
        return appDatabase.stockFromHomeFinalInfoDao.getStockFromHomeFinalInfo()
    }

    private val _submitWithKPYLiveData = MutableLiveData<Resource<String>>()
    val submitWithKPYLiveData: LiveData<Resource<String>>
        get() = _submitWithKPYLiveData

    fun submitWithKPY(
        productIdList: List<MultipartBody.Part>?,
        user_id: String?,
        paid_amount: String?,
        reduced_cost: String?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
       old_stock_qty:List<MultipartBody.Part>?,
       old_stock_size:List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,
        oldStockGoldGemWeightY: List<MultipartBody.Part>?,
        oldStockImpurityWeightY: List<MultipartBody.Part>?,
        oldStockGoldWeightY: List<MultipartBody.Part>?,
        oldStockWastageWeightY: List<MultipartBody.Part>?,
        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockGemDetailQty:List<MultipartBody.Part>?,
        oldStockGemDetailGm:List<MultipartBody.Part>?,
        oldStockGemDetailYwae:List<MultipartBody.Part>?,

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
    ) {
        viewModelScope.launch {
            _submitWithKPYLiveData.value = Resource.Loading()
            _submitWithKPYLiveData.value = normalSaleRepositoryImpl.submitWithKPY(
                productIdList,
                user_id?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                paid_amount?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                reduced_cost?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
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
        }
    }

    fun getOldStockInfoFromDataBase(): List<StockFromHomeInfoUiModel> {
        return appDatabase.stockFromHomeInfoDao.getStockFromHomeInfo().map { it.asUiModel() }
    }
    fun getOldStockFinalInfo():StockFromHomeFinalInfo{
        return appDatabase.stockFromHomeFinalInfoDao.getStockFromHomeFinalInfo()
    }

    fun getCustomerId():String{
        return localDatabase.getAccessCustomerId().orEmpty()
    }

}