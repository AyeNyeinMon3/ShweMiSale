package com.example.shwemisale.screen.sellModule.openVoucher.withKPY

import androidx.lifecycle.*
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
import com.example.shwemisale.util.SingleLiveEvent
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

    private val _submitWithKPYLiveData = SingleLiveEvent<Resource<String>>()
    val submitWithKPYLiveData: SingleLiveEvent<Resource<String>>
        get() = _submitWithKPYLiveData

    private val _getGoldPriceLiveData = MutableLiveData<Resource<GoldPriceDto>>()
    val getGoldPriceLiveData: LiveData<Resource<GoldPriceDto>>
        get() = _getGoldPriceLiveData

    fun getGoldPrice(productIdList:List<String>){
        viewModelScope.launch {
            _getGoldPriceLiveData.value = Resource.Loading()
            _getGoldPriceLiveData.value = normalSaleRepositoryImpl.getGoldPrices(productIdList)
        }
    }



    fun submitWithKPY(
        productIdList: List<MultipartBody.Part>?,
        user_id: String?,
        paid_amount: String?,
        reduced_cost: String?,
        old_voucher_code:String?,
        old_voucher_paid_amount: MultipartBody.Part?,
    ) {
        viewModelScope.launch {
            _submitWithKPYLiveData.value = Resource.Loading()
            _submitWithKPYLiveData.value = normalSaleRepositoryImpl.submitWithKPY(
                productIdList,
                user_id?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                paid_amount?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                reduced_cost?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                old_voucher_paid_amount,
                old_voucher_code?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                localDatabase.getStockFromHomeSessionKey().orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }
    }
    fun getTotalCVoucherBuyingPrice():String{
        return localDatabase.getTotalCVoucherBuyingPriceForStockFromHome().orEmpty()
    }
    fun getTotalGoldWeightYwae():String{
        return localDatabase.getGoldWeightYwaeForStockFromHome().orEmpty()
    }

    fun getCustomerId():String{
        return localDatabase.getAccessCustomerId().orEmpty()
    }

    private val _getUserRedeemPointsLiveData = MutableLiveData<Resource<String>>()
    val getUserRedeemPointsLiveData: LiveData<Resource<String>>
        get() = _getUserRedeemPointsLiveData

    fun getUserRedeemPoints(){
        viewModelScope.launch {
            _getUserRedeemPointsLiveData.value = normalSaleRepositoryImpl.getUserRedeemPoints(
                getCustomerId()
            )
        }
    }

    private val _getRedeemMoneyLiveData = MutableLiveData<Resource<String>>()
    val getRedeemMoneyLiveData: LiveData<Resource<String>>
        get() = _getRedeemMoneyLiveData

    fun getRedeemMoney(redeemAmount:String){
        viewModelScope.launch {
            _getRedeemMoneyLiveData.value = normalSaleRepositoryImpl.getRedeemMoney(
                redeemAmount
            )
        }
    }
    init {
        getUserRedeemPoints()
    }

}