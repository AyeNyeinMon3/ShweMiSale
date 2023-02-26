package com.example.shwemisale.screen.pawnModule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shwemi.util.Resource
import com.example.shwemisale.repositoryImpl.PawnRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CreatePawnViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val pawnRepositoryImpl: PawnRepositoryImpl
):ViewModel() {
    private val _createPawnLiveData = MutableLiveData<Resource<String>>()
    val createPawnLiveData: LiveData<Resource<String>>
        get() = _createPawnLiveData


    fun getPawnInterestRate(){

    }


    fun storePawn(
        user_id:String?,
        total_debt_amount:String?,
        interest_rate:String?,
        warning_period_months:String?,
        interest_free_from:String?,
        interest_free_to:String?,

        old_stocks_nameList:List<MultipartBody.Part>?,
        oldStockImageIds:List<MultipartBody.Part>?,
        oldStockImageFile:List<MultipartBody.Part>?,
        oldStockCondition:List<MultipartBody.Part>?,

        oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        oldStockGemWeightY:List<MultipartBody.Part>?,

        oldStockImpurityWeightY:List<MultipartBody.Part>?,

        oldStockGoldWeightY:List<MultipartBody.Part>?,

        oldStockWastageWeightY:List<MultipartBody.Part>?,

        oldStockRebuyPrice:List<MultipartBody.Part>?,
        oldStockGQinCarat:List<MultipartBody.Part>?,
        oldStockMaintenance_cost:List<MultipartBody.Part>?,
        oldStockGemValue:List<MultipartBody.Part>?,
        oldStockPTAndClipCost:List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        oldStockPriceForPawn:List<MultipartBody.Part>?,
        oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        oldStockABuyingPrice:List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        oldStockDGoldWeightY:List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,

        ){

    }



}