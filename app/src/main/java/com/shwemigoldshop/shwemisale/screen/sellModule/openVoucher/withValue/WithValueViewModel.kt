package com.shwemigoldshop.shwemisale.screen.sellModule.openVoucher.withValue

import androidx.lifecycle.*
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldPriceDto
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.repositoryImpl.AuthRepoImpl
import com.shwemigoldshop.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.shwemigoldshop.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.shwemigoldshop.shwemisale.repositoryImpl.PrintingRepoImpl
import com.shwemigoldshop.shwemisale.room_database.AppDatabase
import com.shwemigoldshop.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class WithValueViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val appDatabase: AppDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val authRepoImpl: AuthRepoImpl,
    private val printingRepoImpl: PrintingRepoImpl,
    private val localDatabase: LocalDatabase
) : ViewModel() {
    private val _stockFromHomeInfoLiveData = SingleLiveEvent<Resource<List<StockFromHomeDomain>>>()
    val stockFromHomeInfoLiveData: SingleLiveEvent<Resource<List<StockFromHomeDomain>>>
        get() = _stockFromHomeInfoLiveData

    private val _pdfDownloadLiveData = SingleLiveEvent<Resource<String>>()
    val pdfDownloadLiveData: SingleLiveEvent<Resource<String>>
        get() = _pdfDownloadLiveData

    fun getPdf(saleId:String){
        viewModelScope.launch {
            _pdfDownloadLiveData.value = Resource.Loading()
            _pdfDownloadLiveData.value=printingRepoImpl.getSalePrint(saleId)
        }
    }
    private val _submitWithValueLiveData = SingleLiveEvent<Resource<String>>()
    val submitWithValueLiveData: SingleLiveEvent<Resource<String>>
        get() = _submitWithValueLiveData

    fun submitWithValue(
        productIdList: List<MultipartBody.Part>?,
        user_id: String?,
        paid_amount: String?,
        reduced_cost: String?,
        redeem_point: String?,
        old_voucher_code:String?,
        old_voucher_paid_amount: MultipartBody.Part?,

    ) {
        viewModelScope.launch {
            _submitWithValueLiveData.value = Resource.Loading()
            _submitWithValueLiveData.value = normalSaleRepositoryImpl.submitWithValue(
                productIdList,
                user_id?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                paid_amount?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                reduced_cost?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                redeem_point?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                old_voucher_paid_amount,
                old_voucher_code?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                localDatabase.getStockFromHomeSessionKey().orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }
    }

    private val _logoutLiveData=SingleLiveEvent<Resource<String>>()
    val logoutLiveData:SingleLiveEvent<Resource<String>>
        get()=_logoutLiveData

    fun logout(){
        _logoutLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _logoutLiveData.value = authRepoImpl.logout()
        }
    }
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
    fun saveTotalGoldWeightYwae(ywae: String) {
        localDatabase.saveGoldWeightYwaeForStockFromHome(ywae)
    }

    fun saveTotalPawnPrice(price: String) {
        localDatabase.saveTotalPawnPriceForStockFromHome(price)
    }
    fun saveTotalBVoucherBuyingPrice(price: String) {
        localDatabase.saveTotalCVoucherBuyingPriceForStockFromHome(price)
    }
    fun getTotalCVoucherBuyingPrice():String{
        return localDatabase.getTotalBVoucherBuyingPriceForStockFromHome().orEmpty()
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
    private val _updateEValueLiveData = SingleLiveEvent<Resource<String>>()
    val updateEValueLiveData: SingleLiveEvent<Resource<String>>
        get() = _updateEValueLiveData

    fun updateEvalue(
        eValue:String
    ){
        viewModelScope.launch {
            _updateEValueLiveData.value = Resource.Loading()
            _updateEValueLiveData.value = normalSaleRepositoryImpl.updateEValue(eValue,localDatabase.getStockFromHomeSessionKey())
        }
    }

    fun getStockFromHomeList() {
        _stockFromHomeInfoLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _stockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.getStockFromHomeList(
                    localDatabase.getStockFromHomeSessionKey().orEmpty()
                )
        }
    }
    init {
        getUserRedeemPoints()
    }
}