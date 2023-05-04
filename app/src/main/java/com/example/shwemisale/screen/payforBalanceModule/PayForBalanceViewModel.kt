package com.example.shwemisale.screen.payforBalanceModule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.dto.RemainingAmountDto
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.PawnRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayForBalanceViewModel @Inject constructor(
    private val pawnRepositoryImpl: PawnRepositoryImpl,
    private val localDatabase: LocalDatabase
) : ViewModel() {
    private val _getRemainAmountLiveData =
        MutableLiveData<Resource<RemainingAmountDto>>()
    val getRemainAmountLiveData: LiveData<Resource<RemainingAmountDto>>
        get() = _getRemainAmountLiveData

    fun getRemainingAmount(
        saleCode: String?
    ) {
        viewModelScope.launch {
            _getRemainAmountLiveData.value = Resource.Loading()
            _getRemainAmountLiveData.value =
                pawnRepositoryImpl.getRemainingAmount(saleCode)
        }
    }

    private val _payBalanceLiveData =
        MutableLiveData<Resource<String>>()
    val payBalanceLiveData: LiveData<Resource<String>>
        get() = _payBalanceLiveData

    fun payBalance(
        sale_id: String, paid_amount: String?
    ) {
        viewModelScope.launch {
            _payBalanceLiveData.value = Resource.Loading()
            _payBalanceLiveData.value =
                pawnRepositoryImpl.payBalance(sale_id, paid_amount)
        }
    }
}