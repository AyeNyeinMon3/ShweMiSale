package com.example.shwemisale.screen.pawnInterestModule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateDto
import com.example.shwemisale.data_layers.dto.pawn.PawnVoucherScanDto
import com.example.shwemisale.repositoryImpl.PawnRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class PawnInterestViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val pawnRepositoryImpl: PawnRepositoryImpl
) : ViewModel() {
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

    private val _getPawnVoucherScanLiveData = MutableLiveData<Resource<PawnVoucherScanDto>>()
    val getPawnVoucherScanLiveData: LiveData<Resource<PawnVoucherScanDto>>
        get() = _getPawnVoucherScanLiveData

    fun pawnVoucherScan(voucherCode: String) {
        viewModelScope.launch {
            _getPawnVoucherScanLiveData.value = Resource.Loading()
            _getPawnVoucherScanLiveData.value = pawnRepositoryImpl.getPawnVoucherScan(voucherCode)
        }
    }

    private val _createPrepaidDebtLiveData = MutableLiveData<Resource<String>>()
    val createPrepaidDebtLiveData: LiveData<Resource<String>>
        get() = _createPrepaidDebtLiveData

    fun createPrepaidDebt(
        voucherCode: String,
        prepaid_debt: String,
        reduced_amount: String
    ) {
        viewModelScope.launch {
            _createPrepaidDebtLiveData.value = Resource.Loading()
            _createPrepaidDebtLiveData.value =
                pawnRepositoryImpl.createPrepaidDebt(voucherCode, prepaid_debt, reduced_amount)
        }
    }

    private val _createPrepaidInterestLiveData = MutableLiveData<Resource<String>>()
    val createPrepaidInterestLiveData: LiveData<Resource<String>>
        get() = _createPrepaidInterestLiveData

    fun createPrepaidInterest(
        voucherCode: String,
        prepaid_debt: String,
        reduced_amount: String
    ) {
        viewModelScope.launch {
            _createPrepaidInterestLiveData.value = Resource.Loading()
            _createPrepaidInterestLiveData.value =
                pawnRepositoryImpl.createPrepaidInterest(voucherCode, prepaid_debt, reduced_amount)
        }
    }

    private val _increaseDebtLiveData = MutableLiveData<Resource<String>>()
    val increaseDebtLiveData: LiveData<Resource<String>>
        get() = _increaseDebtLiveData

    fun increaseDebt(
        voucherCode: String,
        increased_debt: String,
        reduced_amount: String,
        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        oldStockGoldGemWeightY: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,
        oldStockImpurityWeightY: List<MultipartBody.Part>?,
        oldStockGoldWeightY: List<MultipartBody.Part>?,
        oldStockWastageWeightY: List<MultipartBody.Part>?,
        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
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
            _increaseDebtLiveData.value = Resource.Loading()
            _increaseDebtLiveData.value = pawnRepositoryImpl.increaseDebt(
                voucherCode,
                increased_debt,
                reduced_amount,
                old_stocks_nameList,
                oldStockImageIds,
                oldStockImageFile,
                oldStockCondition,
                oldStockGoldGemWeightY,
                oldStockGemWeightY,
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

    private val _payInterestAndIncreaseDebtLiveData = MutableLiveData<Resource<String>>()
    val payInterestAndIncreaseDebtLiveData: LiveData<Resource<String>>
        get() = _payInterestAndIncreaseDebtLiveData

    fun payInterestAndIncreaseDebt(
        voucherCode: String,
        increased_debt: String,
        reduced_amount: String,
        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        oldStockGoldGemWeightY: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,
        oldStockImpurityWeightY: List<MultipartBody.Part>?,
        oldStockGoldWeightY: List<MultipartBody.Part>?,
        oldStockWastageWeightY: List<MultipartBody.Part>?,
        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
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
            _payInterestAndIncreaseDebtLiveData.value = Resource.Loading()
            _payInterestAndIncreaseDebtLiveData.value =
                pawnRepositoryImpl.payInterestAndIncreaseDebt(
                    voucherCode,
                    increased_debt,
                    reduced_amount,
                    old_stocks_nameList,
                    oldStockImageIds,
                    oldStockImageFile,
                    oldStockCondition,
                    oldStockGoldGemWeightY,
                    oldStockGemWeightY,
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

    private val _payInterestLiveData = MutableLiveData<Resource<String>>()
    val payInterestLiveData: LiveData<Resource<String>>
        get() = _payInterestLiveData

    fun payInterest(
        voucherCode: String,
        reduced_amount: String
    ) {
        viewModelScope.launch {
            _payInterestLiveData.value = Resource.Loading()
            _payInterestLiveData.value =
                pawnRepositoryImpl.payInterest(voucherCode, reduced_amount)
        }
    }

    private val _payInterestAndSettleDebtLiveData = MutableLiveData<Resource<String>>()
    val payInterestAndSettleDebtLiveData: LiveData<Resource<String>>
        get() = _payInterestAndSettleDebtLiveData

    fun payInterestAndSettleDebt(
        voucherCode: String,
        reduced_amount: String,
        debt: String
    ) {
        viewModelScope.launch {
            _payInterestAndSettleDebtLiveData.value = Resource.Loading()
            _payInterestAndSettleDebtLiveData.value =
                pawnRepositoryImpl.payInterestAndSettleDebt(voucherCode, reduced_amount, debt)
        }
    }

    private val _payInterestAndReturnStockLiveData = MutableLiveData<Resource<String>>()
    val payInterestAndReturnStockLiveData: LiveData<Resource<String>>
        get() = _payInterestAndReturnStockLiveData

    fun payInterestAndReturnStock(
        voucherCode: String,
        reduced_amount: String,
        debt: String,
        old_stock_id: String
    ) {
        viewModelScope.launch {
            _payInterestAndReturnStockLiveData.value = Resource.Loading()
            _payInterestAndReturnStockLiveData.value =
                pawnRepositoryImpl.payInterestAndReturnStock(
                    voucherCode,
                    reduced_amount,
                    debt,
                    old_stock_id
                )
        }
    }

    private val _settleLiveData = MutableLiveData<Resource<String>>()
    val settleLiveData: LiveData<Resource<String>>
        get() = _settleLiveData

    fun settle(
        voucherCode: String,
        reduced_amount: String
    ) {
        viewModelScope.launch {
            _settleLiveData.value = Resource.Loading()
            _settleLiveData.value =
                pawnRepositoryImpl.settle(voucherCode, reduced_amount)
        }
    }

    private val _sellOldStockLiveData = MutableLiveData<Resource<String>>()
    val sellOldStockLiveData: LiveData<Resource<String>>
        get() = _sellOldStockLiveData

    fun sellOldStock(
        voucherCode: String,
        reduced_amount: String,
        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        oldStockGoldGemWeightY: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,
        oldStockImpurityWeightY: List<MultipartBody.Part>?,
        oldStockGoldWeightY: List<MultipartBody.Part>?,
        oldStockWastageWeightY: List<MultipartBody.Part>?,
        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
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
            _sellOldStockLiveData.value = Resource.Loading()
            _sellOldStockLiveData.value =
                pawnRepositoryImpl.sellOldStock(
                    voucherCode,
                    reduced_amount,
                    old_stocks_nameList,
                    oldStockImageIds,
                    oldStockImageFile,
                    oldStockCondition,
                    oldStockGoldGemWeightY,
                    oldStockGemWeightY,
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
}