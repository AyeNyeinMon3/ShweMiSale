package com.example.shwemisale.screen.pawnInterestModule

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.PawnVoucherScanDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateDto
import com.example.shwemisale.data_layers.dto.pawn.PawnVoucherScanDto
import com.example.shwemisale.data_layers.dto.printing.PawnCreatePrintDto
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.AuthRepoImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.repositoryImpl.PawnRepositoryImpl
import com.example.shwemisale.repositoryImpl.PrintingRepoImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PawnInterestViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val pawnRepositoryImpl: PawnRepositoryImpl,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase,
    private val authRepoImpl: AuthRepoImpl,
    private val printingRepoImpl: PrintingRepoImpl
) : ViewModel() {
    var pawnData: PawnVoucherScanDomain? = null

    private val _logoutLiveData=SingleLiveEvent<Resource<String>>()
    val logoutLiveData:SingleLiveEvent<Resource<String>>
        get()=_logoutLiveData

    fun logout(){
        _logoutLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _logoutLiveData.value = authRepoImpl.logout()
        }
    }
    private val _pdfDownloadLiveData = SingleLiveEvent<Resource<String>>()
    val pdfDownloadLiveData: SingleLiveEvent<Resource<String>>
        get() = _pdfDownloadLiveData

    fun getPdf(pawnId:String){
        viewModelScope.launch {
            _pdfDownloadLiveData.value = Resource.Loading()
            _pdfDownloadLiveData.value=printingRepoImpl.getPawnPrint(pawnId)
        }
    }
    private val _printPawnSaleLiveData=SingleLiveEvent<Resource<PawnCreatePrintDto>>()
    val printPawnSaleLiveData:SingleLiveEvent<Resource<PawnCreatePrintDto>>
        get()=_printPawnSaleLiveData

    fun printPawnSale(pawnSaleId:String){
        _printPawnSaleLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _printPawnSaleLiveData.value = printingRepoImpl.getPawnItemSalePrint(pawnSaleId)
        }
    }

    private val _getPawnVoucherScanLiveData = SingleLiveEvent<Resource<PawnVoucherScanDto>>()
    val getPawnVoucherScanLiveData: SingleLiveEvent<Resource<PawnVoucherScanDto>>
        get() = _getPawnVoucherScanLiveData
    fun getTotalPawnPrice():String {
        return localDatabase.getTotalPawnPriceForStockFromHome()
    }

    fun getTotalVoucherBuyingPrice():String {
        return localDatabase.getTotalVoucherBuyingPriceForPawn()
    }
    fun getPawnPriceForRemainedPawnItem():String {
        return localDatabase.getRemainedPawnItemsPrice()
    }

    fun pawnVoucherScan(voucherCode: String) {
        viewModelScope.launch {
            _getPawnVoucherScanLiveData.value = Resource.Loading()
            _getPawnVoucherScanLiveData.value = pawnRepositoryImpl.getPawnVoucherScan(voucherCode)
        }
    }

    private val _createPrepaidDebtLiveData = SingleLiveEvent<Resource<String>>()
    val createPrepaidDebtLiveData: LiveData<Resource<String>>
        get() = _createPrepaidDebtLiveData

    fun createPrepaidDebt(
        voucherCode: String,
        prepaid_debt: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _createPrepaidDebtLiveData.value = Resource.Loading()
            _createPrepaidDebtLiveData.value =
                pawnRepositoryImpl.createPrepaidDebt(
                    voucherCode,
                    prepaid_debt,
                    reduced_amount,
                    is_app_functions_allowed
                )
        }
    }

    private val _createPrepaidInterestLiveData = SingleLiveEvent<Resource<String>>()
    val createPrepaidInterestLiveData: LiveData<Resource<String>>
        get() = _createPrepaidInterestLiveData

    fun createPrepaidInterest(
        voucherCode: String,
        number_of_months: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _createPrepaidInterestLiveData.value = Resource.Loading()
            _createPrepaidInterestLiveData.value =
                pawnRepositoryImpl.createPrepaidInterest(
                    voucherCode,
                    number_of_months,
                    reduced_amount,
                    is_app_functions_allowed
                )
        }
    }

    private val _increaseDebtLiveData = SingleLiveEvent<Resource<String>>()
    val increaseDebtLiveData: SingleLiveEvent<Resource<String>>
        get() = _increaseDebtLiveData

    fun increaseDebt(
        voucherCode: String,
        increased_debt: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,
    ) {
        viewModelScope.launch {
            _increaseDebtLiveData.value = Resource.Loading()
            _increaseDebtLiveData.value = pawnRepositoryImpl.increaseDebt(
                voucherCode,
                increased_debt,
                reduced_amount,
                is_app_functions_allowed,
                localDatabase.getPawnOldStockSessionKey().orEmpty()
            )
        }
    }

    private val _payInterestAndIncreaseDebtLiveData = SingleLiveEvent<Resource<String>>()
    val payInterestAndIncreaseDebtLiveData: SingleLiveEvent<Resource<String>>
        get() = _payInterestAndIncreaseDebtLiveData

    fun payInterestAndIncreaseDebt(
        voucherCode: String,
        increased_debt: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _payInterestAndIncreaseDebtLiveData.value = Resource.Loading()
            _payInterestAndIncreaseDebtLiveData.value =
                pawnRepositoryImpl.payInterestAndIncreaseDebt(
                    voucherCode,
                    increased_debt,
                    reduced_amount,
                    is_app_functions_allowed,
                    localDatabase.getPawnOldStockSessionKey().orEmpty()
                )
        }
    }

    private val _payInterestLiveData = SingleLiveEvent<Resource<String>>()
    val payInterestLiveData: SingleLiveEvent<Resource<String>>
        get() = _payInterestLiveData

    fun payInterest(
        voucherCode: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _payInterestLiveData.value = Resource.Loading()
            _payInterestLiveData.value =
                pawnRepositoryImpl.payInterest(
                    voucherCode,
                    reduced_amount,
                    is_app_functions_allowed
                )
        }
    }

    private val _payInterestAndSettleDebtLiveData = SingleLiveEvent<Resource<String>>()
    val payInterestAndSettleDebtLiveData: SingleLiveEvent<Resource<String>>
        get() = _payInterestAndSettleDebtLiveData

    fun payInterestAndSettleDebt(
        voucherCode: String,
        reduced_amount: String,
        debt: String,
        is_app_functions_allowed: String?
    ) {
        viewModelScope.launch {
            _payInterestAndSettleDebtLiveData.value = Resource.Loading()
            _payInterestAndSettleDebtLiveData.value =
                pawnRepositoryImpl.payInterestAndSettleDebt(
                    voucherCode,
                    reduced_amount,
                    debt,
                    is_app_functions_allowed
                )
        }
    }

    private val _payInterestAndReturnStockLiveData = SingleLiveEvent<Resource<String>>()
    val payInterestAndReturnStockLiveData: SingleLiveEvent<Resource<String>>
        get() = _payInterestAndReturnStockLiveData

    fun payInterestAndReturnStock(
        voucherCode: String,
        reduced_amount: String,
        debt: String,
        old_stock_id: List<String>,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _payInterestAndReturnStockLiveData.value = Resource.Loading()
            _payInterestAndReturnStockLiveData.value =
                pawnRepositoryImpl.payInterestAndReturnStock(
                    voucherCode,
                    reduced_amount,
                    debt,
                    is_app_functions_allowed,
                    old_stock_id
                )
        }
    }

    private val _settleLiveData = SingleLiveEvent<Resource<String>>()
    val settleLiveData: SingleLiveEvent<Resource<String>>
        get() = _settleLiveData

    fun settle(
        voucherCode: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _settleLiveData.value = Resource.Loading()
            _settleLiveData.value =
                pawnRepositoryImpl.settle(voucherCode, reduced_amount, is_app_functions_allowed)
        }
    }

    private val _sellOldStockLiveData = SingleLiveEvent<Resource<String>>()
    val sellOldStockLiveData: SingleLiveEvent<Resource<String>>
        get() = _sellOldStockLiveData

    fun sellOldStock(
        voucherCode: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,
        old_stock_id: List<String>,

        ) {
        viewModelScope.launch {
            _sellOldStockLiveData.value = Resource.Loading()
            _sellOldStockLiveData.value =
                pawnRepositoryImpl.sellOldStock(
                    voucherCode,
                    reduced_amount,
                    is_app_functions_allowed,
                    old_stock_id
                )
        }
    }

    private val _pawnStockFromHomeInfoLiveData =
        SingleLiveEvent<Resource<List<StockFromHomeDomain>>>()
    val pawnStockFromHomeInfoLiveData: SingleLiveEvent<Resource<List<StockFromHomeDomain>>>
        get() = _pawnStockFromHomeInfoLiveData

    fun getStockFromHomeForPawnList(pawnVoucherCode: String,isPawnSale:Boolean) {
        _pawnStockFromHomeInfoLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _pawnStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.getStockFromHomeForPawn(pawnVoucherCode,if (isPawnSale) "1" else "0")
        }
    }

}