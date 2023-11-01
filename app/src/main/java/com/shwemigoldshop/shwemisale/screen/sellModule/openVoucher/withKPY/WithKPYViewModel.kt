package com.shwemigoldshop.shwemisale.screen.sellModule.openVoucher.withKPY

import androidx.lifecycle.*
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.getRoundDownForPrice
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldPriceDto
import com.shwemigoldshop.shwemisale.data_layers.ui_models.product.ProductInfoUiModel
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
class WithKPYViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val appDatabase: AppDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val authRepoImpl: AuthRepoImpl,
    private val printingRepoImpl: PrintingRepoImpl,
    private val localDatabase: LocalDatabase
) : ViewModel() {


    private val _pdfDownloadLiveData = SingleLiveEvent<Resource<String>>()
    val pdfDownloadLiveData: SingleLiveEvent<Resource<String>>
        get() = _pdfDownloadLiveData

    fun getPdf(saleId: String) {
        viewModelScope.launch {
            _pdfDownloadLiveData.value = Resource.Loading()
            _pdfDownloadLiveData.value = printingRepoImpl.getSalePrint(saleId)
        }
    }

    private val _logoutLiveData = SingleLiveEvent<Resource<String>>()
    val logoutLiveData: SingleLiveEvent<Resource<String>>
        get() = _logoutLiveData

    fun logout() {
        _logoutLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _logoutLiveData.value = authRepoImpl.logout()
        }
    }

    private val _submitWithKPYLiveData = SingleLiveEvent<Resource<String>>()
    val submitWithKPYLiveData: SingleLiveEvent<Resource<String>>
        get() = _submitWithKPYLiveData

    private val _getGoldPriceLiveData = MutableLiveData<Resource<GoldPriceDto>>()
    val getGoldPriceLiveData: LiveData<Resource<GoldPriceDto>>
        get() = _getGoldPriceLiveData

    fun getGoldPrice(productIdList: List<String>) {
        viewModelScope.launch {
            _getGoldPriceLiveData.value = Resource.Loading()
            _getGoldPriceLiveData.value = normalSaleRepositoryImpl.getGoldPrices(productIdList)
        }
    }


    fun submitWithKPY(
        productIdList: List<MultipartBody.Part>?,
        redeem_point: String?,
        old_voucher_code: String?,
        old_voucher_paid_amount: MultipartBody.Part?,
    ) {
        viewModelScope.launch {
            _submitWithKPYLiveData.value = Resource.Loading()
            _submitWithKPYLiveData.value = normalSaleRepositoryImpl.submitWithKPY(
                productIdList,
                getCustomerId().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                _paymentFromCustomerLiveData.value?.let {
                    it.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                },
                _manualReduceMoneyLiveData.value?.let {
                    it.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                },
                redeem_point?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                old_voucher_paid_amount,
                old_voucher_code?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                localDatabase.getStockFromHomeSessionKey()
                    ?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                _oldStockCalculationStateLiveData.value!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }
    }

    fun getTotalCVoucherBuyingPrice(): String {
        return localDatabase.getTotalBVoucherBuyingPriceForStockFromHome().orEmpty()
    }

    fun getTotalGoldWeightYwae(): String {
        return localDatabase.getGoldWeightYwaeForStockFromHome().orEmpty()
    }

    fun getCustomerId(): String {
        return localDatabase.getAccessCustomerId().orEmpty()
    }

    private val _getUserRedeemPointsLiveData = MutableLiveData<Resource<String>>()
    val getUserRedeemPointsLiveData: LiveData<Resource<String>>
        get() = _getUserRedeemPointsLiveData

    fun getUserRedeemPoints() {
        viewModelScope.launch {
            _getUserRedeemPointsLiveData.value = normalSaleRepositoryImpl.getUserRedeemPoints(
                getCustomerId()
            )
        }
    }

    private val _getRedeemMoneyLiveData = MutableLiveData<Resource<String>>()
    val getRedeemMoneyLiveData: LiveData<Resource<String>>
        get() = _getRedeemMoneyLiveData

    fun getRedeemMoney(redeemAmount: String) {
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
        eValue: String
    ) {
        viewModelScope.launch {
            _updateEValueLiveData.value = Resource.Loading()
            _updateEValueLiveData.value = normalSaleRepositoryImpl.updateEValue(
                eValue,
                localDatabase.getStockFromHomeSessionKey()
            )
        }
    }

    private val _oldStockCalculationStateLiveData = MutableLiveData<String>("with_kpy")
    val oldStockCalculationStateLiveData: LiveData<String>
        get() = _oldStockCalculationStateLiveData

    fun setOldStockCalculationState(calculationState: String) {
        _oldStockCalculationStateLiveData.value = calculationState
    }


    private val _oldStockWeightYwaeLiveData = MutableLiveData<Double>()
    val oldStockWeightYwaeLiveData: LiveData<Double>
        get() = _oldStockWeightYwaeLiveData

    fun setOldStockWeightYwae(ywae: Double) {
        _oldStockWeightYwaeLiveData.value = ywae
    }

    private val _poloGoldWeightYwaeLiveData = MutableLiveData<Double>()
    val poloGoldWeightYwaeLiveData: LiveData<Double>
        get() = _poloGoldWeightYwaeLiveData


    private val _totalGoldWeightOfProductsLiveData = MutableLiveData<Double>()
    val totalGoldWeightOfProductsLiveData: LiveData<Double>
        get() = _totalGoldWeightOfProductsLiveData

    private val _totalGemWeightOfProductsLiveData = MutableLiveData<Double>()
    val totalGemWeightOfProductsLiveData: LiveData<Double>
        get() = _totalGoldWeightOfProductsLiveData

    private val _totalWastageWeightOfProductsLiveData = MutableLiveData<Double>()
    val totalWastageWeightOfProductsLiveData: LiveData<Double>
        get() = _totalWastageWeightOfProductsLiveData

    private val _totalGoldAndWastageWeightOfProductsLiveData = MutableLiveData<Double>()
    val totalGoldAndWastageWeightOfProductsLiveData: LiveData<Double>
        get() = _totalGoldAndWastageWeightOfProductsLiveData

    private val _totalGemValueOfProductsLiveData = MutableLiveData<Int>()
    val totalGemValueOfProductsLiveData: LiveData<Int>
        get() = _totalGemValueOfProductsLiveData

    private val _totalMaintainenceOfProductsLiveData = MutableLiveData<Int>()
    val totalMaintainenceOfProductsLiveData: LiveData<Int>
        get() = _totalMaintainenceOfProductsLiveData

    private val _totalPtClipCostOfProductsLiveData = MutableLiveData<Int>()
    val totalPtClipCostOfProductsLiveData: LiveData<Int>
        get() = _totalPtClipCostOfProductsLiveData

    private val _oldVoucherPaymentLivedata = MutableLiveData<Int>()
    val oldVoucherPaymentLivedata: LiveData<Int>
        get() = _oldVoucherPaymentLivedata

    fun setOldVoucherPaymentLiveData(money: Int) {
        _oldVoucherPaymentLivedata.value = money
    }

    private val _oldStocksValueLiveData = MutableLiveData<Int>()
    val oldStocksValueLiveData: LiveData<Int>
        get() = _oldStocksValueLiveData

    fun setOldStockValueLiveData(money: Int) {
        _oldStocksValueLiveData.value = money
    }

    private val _poloValueLiveData = MutableLiveData<Int>()
    val poloValueLiveData: LiveData<Int>
        get() = _poloValueLiveData

    fun calculatePoloValueWithKpy() {
        val goldPrice = _getGoldPriceLiveData.value?.data?.gold_price ?: "0"
        val poloValue = (_poloGoldWeightYwaeLiveData.value!! / 128) * goldPrice.toInt()
        _poloValueLiveData.value = getRoundDownForPrice(poloValue.toInt())
    }

    fun calculatePoloValueWithValue() {
        val goldPrice = _getGoldPriceLiveData.value?.data?.gold_price ?: "0"

        val productPrices = (_totalGoldAndWastageWeightOfProductsLiveData.value!! / 128) * goldPrice.toInt()
        val poloValue = productPrices
        _poloValueLiveData.value = getRoundDownForPrice(poloValue.toInt())
    }

    private val _totalValuesLiveData = MutableLiveData<Int>()
    val totalValuesLiveData: LiveData<Int>
        get() = _totalValuesLiveData

    fun calculateTotalValue(){
        val totalValues = _poloValueLiveData.value!! + _totalGemValueOfProductsLiveData.value!! + _totalMaintainenceOfProductsLiveData.value!! + _totalPtClipCostOfProductsLiveData.value!! - _oldVoucherPaymentLivedata.value!!
        _totalValuesLiveData.value = getRoundDownForPrice(totalValues)
    }


    //Reduced Moneys
    private val _flashSaleDiscountLiveData = MutableLiveData<Int>(0)
    val flashSaleDiscountLiveData: LiveData<Int>
        get() = _flashSaleDiscountLiveData

    private val _manualReduceMoneyLiveData = MutableLiveData<Int>(0)
    val manualReduceMoneyLiveData: LiveData<Int>
        get() = _manualReduceMoneyLiveData

    fun setManualReducedMoney(money:Int){
        _manualReduceMoneyLiveData.value = money
    }

    private val _taxMoneyLiveData = MutableLiveData<Int>(0)
    val taxMoneyLiveData: LiveData<Int>
        get() = _taxMoneyLiveData

    fun setTaxMoney(money:Int){
        _taxMoneyLiveData.value = money
        if ((_totalCostToPayLiveData.value?:0) != 0 ){
            _totalCostToPayIncludingTaxMoney.value = _totalCostToPayLiveData.value!! + money
        }
    }

    private val _totalCostToPayIncludingTaxMoney = MutableLiveData<Int>()
    val totalCostToPayIncludingTaxMoney: LiveData<Int>
        get() = _totalCostToPayIncludingTaxMoney


    private val _totalReduceMoneyLiveData = MutableLiveData<Int>()
    val totalReduceMoneyLiveData: LiveData<Int>
        get() = _totalReduceMoneyLiveData

    fun setTotalReduceMoney(money:Int){
        _totalReduceMoneyLiveData.value = money
    }

    fun calculateProducts(productList: List<ProductInfoUiModel>) {
        var totalGoldWeight = 0.0
        var totalGemWeight = 0.0
        var totalWastageWeight = 0.0
        var totalMaintenanceFees = 0.0
        var totalGemValue = 0
        var totalPtClipFees = 0
        var totalFlashSaleDiscount = 0
        productList.forEach {
            totalGoldWeight += it.gold_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() }
            totalGemWeight += it.gem_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() }
            totalWastageWeight += it.wastage_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() }
            totalMaintenanceFees += it.maintenance_cost.let { if (it.isEmpty()) 0.0 else it.toDouble() }
            totalGemValue += it.gem_value.let { if (it.isEmpty()) 0 else it.toInt() }
            totalPtClipFees += it.pt_and_clip_cost.let { if (it.isEmpty()) 0 else it.toInt() }
            totalFlashSaleDiscount = it.promotion_discount.let { if (it.isEmpty()) 0 else it.toInt() }
        }
        var totalGoldAndWastageWeight = totalGoldWeight + totalWastageWeight
        _totalGoldWeightOfProductsLiveData.value = totalGoldWeight
        _totalGemWeightOfProductsLiveData.value = totalGemWeight
        _totalWastageWeightOfProductsLiveData.value = totalWastageWeight
        _totalMaintainenceOfProductsLiveData.value = totalMaintenanceFees.toInt()
        _totalGemValueOfProductsLiveData.value = totalGemValue
        _totalPtClipCostOfProductsLiveData.value = totalPtClipFees
        _totalGoldAndWastageWeightOfProductsLiveData.value = totalGoldAndWastageWeight

        //reducedMoney
        _flashSaleDiscountLiveData.value = totalFlashSaleDiscount

        var poloGoldWeight = totalGoldAndWastageWeight - _oldStockWeightYwaeLiveData.value!!
        _poloGoldWeightYwaeLiveData.value = poloGoldWeight

    }

    private val _totalCostToPayLiveData = MutableLiveData<Int>(0)
    val totalCostToPayLiveData: LiveData<Int>
        get() = _totalCostToPayLiveData

    fun setTotalCostToPay(money:Int){
        _totalCostToPayLiveData.value = money
    }

    private val _paymentFromCustomerLiveData = MutableLiveData<Int>()
    val paymentFromCustomerLiveData: LiveData<Int>
        get() = _paymentFromCustomerLiveData

    fun setPaymentFromCustomer(money:Int){
        _paymentFromCustomerLiveData.value = money
    }
    fun setFullPayment(){
        _paymentFromCustomerLiveData.value = if (_totalCostToPayLiveData.value!! < 0) 0  else _totalCostToPayLiveData.value!!
    }

    private val _remainMoneyLiveData = MutableLiveData<Int>()
    val remainMoneyLiveData: LiveData<Int>
        get() = _remainMoneyLiveData

    private val _calculationStateLiveData = MutableLiveData<String>()
    val calculationStateLiveData: LiveData<String>
        get() = _calculationStateLiveData

    fun setCalculationState(){
        val isFinalCalculation = (_totalCostToPayLiveData.value ?: 0) != 0
        if (!isFinalCalculation){
            _calculationStateLiveData.value = "firstCalculation"
        }else{
            _calculationStateLiveData.value = "finalCalculation"
        }
    }

    fun firstCalculation(manualReduceMoney:Int){
        setManualReducedMoney(manualReduceMoney)

        //totalReduceMoney
        val totalReduceMoney = _manualReduceMoneyLiveData.value!! + _flashSaleDiscountLiveData.value!!
        setTotalReduceMoney(totalReduceMoney)

        calculateTotalValue()


        //totalCostToPay
        val totalCostToPay = _totalValuesLiveData.value!! - _totalReduceMoneyLiveData.value!!
        _totalCostToPayIncludingTaxMoney.value = totalCostToPay
        setTotalCostToPay(totalCostToPay)
    }

    fun finalCalculation(paymentFromCustomer:Int,manualReduceMoney: Int){
        setManualReducedMoney(manualReduceMoney)
        //totalReduceMoney
        val totalReduceMoney = _manualReduceMoneyLiveData.value!! + _flashSaleDiscountLiveData.value!! + _taxMoneyLiveData.value!!
        setTotalReduceMoney(totalReduceMoney)

        //totalCostToPay
        val totalCostToPay = _totalValuesLiveData.value!! - _totalReduceMoneyLiveData.value!!
        setTotalCostToPay(totalCostToPay)
        _totalCostToPayIncludingTaxMoney.value = totalCostToPay


        //paymentFromCustomer
        setPaymentFromCustomer(paymentFromCustomer)

        //RemainMoney
        val remainMoney = _totalCostToPayLiveData.value!! - _paymentFromCustomerLiveData.value!!
        _remainMoneyLiveData.value = remainMoney

    }


    init {
        getUserRedeemPoints()
    }

}