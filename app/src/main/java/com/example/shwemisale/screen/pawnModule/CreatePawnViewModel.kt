package com.example.shwemisale.screen.pawnModule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.PawnRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CreatePawnViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val pawnRepositoryImpl: PawnRepositoryImpl,
    private val localDatabase: LocalDatabase
):ViewModel() {
    private val _createPawnLiveData = MutableLiveData<Resource<String>>()
    val createPawnLiveData: LiveData<Resource<String>>
        get() = _createPawnLiveData


    private val _getPawnInterestRateLiveData =
        MutableLiveData<Resource<List<PawnInterestRateDto>>>()
    val getPawnInterestRateLiveData: LiveData<Resource<List<PawnInterestRateDto>>>
        get() = _getPawnInterestRateLiveData

    fun getPawnInterestRate() {
        viewModelScope.launch {
            _getPawnInterestRateLiveData.value = Resource.Loading()
            _getPawnInterestRateLiveData.value = pawnRepositoryImpl.getPawnInterestRate()
        }
    }


    fun storePawn(

        total_debt_amount:String?,
        interest_rate:String?,
        warning_period_months:String?,
        interest_free_from:String?,
        interest_free_to:String?,
        is_app_functions_allowed:String?
        ){
        viewModelScope.launch {
            _createPawnLiveData.value = Resource.Loading()
            _createPawnLiveData.value = pawnRepositoryImpl.storePawn(
                localDatabase.getAccessCustomerId().orEmpty(),
            total_debt_amount,interest_rate,warning_period_months,interest_free_from,interest_free_to,
                is_app_functions_allowed,
            localDatabase.getStockFromHomeSessionKey().orEmpty())
        }
    }
    fun getPawnPrice():String{
        return localDatabase.getTotalPawnPriceForStockFromHome().orEmpty()
    }


}