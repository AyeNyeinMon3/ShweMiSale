package com.example.shwemisale.screen.goldFromHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.StockWeightByVoucherDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.asUiModel
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo
import com.example.shwemisale.room_database.entity.asUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoldFromHomeViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val appDatabase: AppDatabase
) :ViewModel(){
    var count = 0
    private val _stockWeightByVoucherLiveData = MutableLiveData<Resource<List<StockWeightByVoucherUiModel>>>()
    val stockWeightByVoucherLiveData: LiveData<Resource<List<StockWeightByVoucherUiModel>>>
        get() = _stockWeightByVoucherLiveData

    fun resetstockWeightByVoucherLiveData(){
        _stockWeightByVoucherLiveData.value = null
    }

    fun getStockWeightByVoucher(voucherCode:String){
        _stockWeightByVoucherLiveData.value=Resource.Loading()
        viewModelScope.launch {
            val result = goldFromHomeRepositoryImpl.getStockWeightByVoucher(voucherCode)
            when (result) {
                is Resource.Success -> {

                    _stockWeightByVoucherLiveData.value = Resource.Success(result.data!!.map { it.asUiModel() })
                }
                is Resource.Error -> {
                    _stockWeightByVoucherLiveData.value = Resource.Error(result.message)
                }
                else -> {}
            }
        }
    }

    var stockFromHomeListInAppDatabase = listOf<StockFromHomeInfoUiModel>()
    fun getStockFromHomeListFromAppdatabase(){
        stockFromHomeListInAppDatabase =  appDatabase.stockFromHomeInfoDao.getStockFromHomeInfo().map { it.asUiModel() }
    }

    private val _stockInfoByVoucherLiveData = MutableLiveData<Resource<List<StockFromHomeInfoUiModel>>>()
    val stockInfoByVoucherLiveData: LiveData<Resource<List<StockFromHomeInfoUiModel>>>
        get() = _stockInfoByVoucherLiveData

    fun getStockInfoByVoucher(voucherCode:String,productIdList:List<String>){
        _stockInfoByVoucherLiveData.value=Resource.Loading()
        viewModelScope.launch {
            val result = goldFromHomeRepositoryImpl.getStockInfoByVoucher(voucherCode,productIdList)
            when (result) {
                is Resource.Success -> {
                    _stockInfoByVoucherLiveData.value = Resource.Success(appDatabase.stockFromHomeInfoDao.getStockFromHomeInfo().map { it.asUiModel() })
                }
                is Resource.Error -> {
                    _stockInfoByVoucherLiveData.value = Resource.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun deleteStock(id:String){
        viewModelScope.launch {
            appDatabase.stockFromHomeInfoDao.deleteStockFromHomeInfo(id)
            _stockInfoByVoucherLiveData.value = Resource.Success(appDatabase.stockFromHomeInfoDao.getStockFromHomeInfo().map { it.asUiModel() })
        }
    }
    fun saveStockFromHomeInfoFinal(
        finalPawnPrice:String,
        finalGoldWeightY:String,
        finalVoucherPaidAmount:String
    ){
        viewModelScope.launch {
            count++
            appDatabase.stockFromHomeFinalInfoDao.saveStockFromHomeFinalInfo(
                StockFromHomeFinalInfo(count.toLong(),finalPawnPrice, finalGoldWeightY, finalVoucherPaidAmount)
            )
        }
    }

    fun getStockFromHomeInfoFinal():StockFromHomeFinalInfo{
        return appDatabase.stockFromHomeFinalInfoDao.getStockFromHomeFinalInfo()
    }

    private val _oldVoucherPaidAmountLiveData = MutableLiveData<Resource<String>>()
    val oldVoucherPaidAmountLiveData: LiveData<Resource<String>>
        get() = _oldVoucherPaidAmountLiveData
    fun getOldVoucherPaidAmount(voucherCode:String,id:String){
        _oldVoucherPaidAmountLiveData.value=Resource.Loading()
        viewModelScope.launch {
            val result = normalSaleRepositoryImpl.getPaidAmountOfVoucher(voucherCode)
            when (result) {
                is Resource.Success -> {
                    _oldVoucherPaidAmountLiveData.value = Resource.Success(result.data)
                }
                is Resource.Error -> {
                    _oldVoucherPaidAmountLiveData.value = Resource.Error(result.message)
                }
                else -> {}
            }
        }
    }
}