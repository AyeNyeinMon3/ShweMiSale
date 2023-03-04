package com.example.shwemisale.screen.sellModule.exchangeOrderAndOldItem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeOrderViewModel @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl
):ViewModel(){
    private val _scanVoucherLiveData = MutableLiveData<Resource<String>>()
    val scanVoucherLiveData: LiveData<Resource<String>>
        get() = _scanVoucherLiveData

    fun scanVoucher(voucherCode:String){
        viewModelScope.launch {
            _scanVoucherLiveData.value = Resource.Loading()
            _scanVoucherLiveData.value = normalSaleRepositoryImpl.getPaidAmountOfVoucher(voucherCode)
        }
    }

}