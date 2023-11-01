package com.shwemigoldshop.shwemisale.screen.sellModule.exchangeOrderAndOldItem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.PaidAmountOfVoucherDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.shwemigoldshop.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.shwemigoldshop.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeOrderViewModel @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl
) : ViewModel() {
    private val _stockFromHomeInfoLiveData = SingleLiveEvent<Resource<List<StockFromHomeDomain>>>()
    val stockFromHomeInfoLiveData: SingleLiveEvent<Resource<List<StockFromHomeDomain>>>
        get() = _stockFromHomeInfoLiveData

    private val _scanVoucherLiveData = SingleLiveEvent<Resource<PaidAmountOfVoucherDomain>>()
    val scanVoucherLiveData: SingleLiveEvent<Resource<PaidAmountOfVoucherDomain>>
        get() = _scanVoucherLiveData


    fun scanVoucher(voucherCode: String) {
        viewModelScope.launch {
            _scanVoucherLiveData.value = Resource.Loading()
            _scanVoucherLiveData.value =
                normalSaleRepositoryImpl.getPaidAmountOfVoucher(voucherCode)
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



    fun getStockFromHomeList() {
        _stockFromHomeInfoLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _stockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.getStockFromHomeList(
                    localDatabase.getStockFromHomeSessionKey().orEmpty()
                )
        }
    }

    private val _goldTypePriceLiveData = MutableLiveData<Resource<List<GoldTypePriceDto>>>()
    val goldTypePriceLiveData: LiveData<Resource<List<GoldTypePriceDto>>>
        get() = _goldTypePriceLiveData

    fun getGoldTypePrice() {
        _goldTypePriceLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _goldTypePriceLiveData.value = goldFromHomeRepositoryImpl.getGoldType(null)
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

}

