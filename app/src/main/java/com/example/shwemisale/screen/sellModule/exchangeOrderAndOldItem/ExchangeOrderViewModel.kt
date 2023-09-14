package com.example.shwemisale.screen.sellModule.exchangeOrderAndOldItem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.PaidAmountOfVoucherDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt

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

