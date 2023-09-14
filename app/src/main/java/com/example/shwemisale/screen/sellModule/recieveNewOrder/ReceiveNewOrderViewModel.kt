package com.example.shwemisale.screen.sellModule.recieveNewOrder

import androidx.lifecycle.*
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.domain.sample.SampleDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.AuthRepoImpl
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.repositoryImpl.PrintingRepoImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class ReceiveNewOrderViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val localDatabase: LocalDatabase,
    private val printingRepoImpl: PrintingRepoImpl,
    private val authRepoImpl: AuthRepoImpl
) : ViewModel() {
//    var goldFromHomeWithKpy = "0"
//    var goldFromHomeWithValue = "0"
    var goldPrice = 0
    var takenSamples = listOf<SampleDomain>()
    var samplesFromRoom = normalSaleRepositoryImpl.getSamplesFromRoom()
    private val _stockFromHomeInfoLiveData = SingleLiveEvent<Resource<List<StockFromHomeDomain>>>()
    val stockFromHomeInfoLiveData: SingleLiveEvent<Resource<List<StockFromHomeDomain>>>
        get() = _stockFromHomeInfoLiveData
    private val _updateStockFromHomeInfoLiveData =
        SingleLiveEvent<Resource<String>>()
    val updateStockFromHomeInfoLiveData: SingleLiveEvent<Resource<String>>
        get() = _updateStockFromHomeInfoLiveData
    private val _receiveNewOrderLiveData = MutableLiveData<Resource<String>>()
    val receiveNewOrderLiveData: LiveData<Resource<String>>
        get() = _receiveNewOrderLiveData
    private val _pdfDownloadLiveData = SingleLiveEvent<Resource<String>>()
    val pdfDownloadLiveData: SingleLiveEvent<Resource<String>>
        get() = _pdfDownloadLiveData

    fun getPdf(saleId:String){
        viewModelScope.launch {
            _pdfDownloadLiveData.value = Resource.Loading()
            _pdfDownloadLiveData.value=printingRepoImpl.getSalePrint(saleId)
        }
    }
    private val _logoutLiveData=SingleLiveEvent<Resource<String>>()
    val logoutLiveData:SingleLiveEvent<Resource<String>>
        get()=_logoutLiveData

    fun logout(){
        _logoutLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _logoutLiveData.value = authRepoImpl.logout()
        }
    }
    fun addTotalGoldWeightYwaeToStockFromHome(ywae:String){
        localDatabase.saveGoldWeightYwaeForStockFromHome(ywae)
    }
    fun removeSampleFromRoom(sample:SampleDomain){
        viewModelScope.launch {
            appDatabase.sampleDao.deleteSamples(sample.localId.orEmpty())
            samplesFromRoom =  normalSaleRepositoryImpl.getSamplesFromRoom()
        }
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
    fun submit(
        name: String,
        gold_type_id: String,
        gold_price: String,
        total_gold_weight_ywae: String,
        est_unit_wastage_ywae: String,
        qty: String,
        gem_value: String,
        maintenance_cost: String,
        date_of_delivery: String,
        remark: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stock_calc_type: String,
        oldStockSampleListId: List<MultipartBody.Part>?

    ) {
        viewModelScope.launch {
            _receiveNewOrderLiveData.value = Resource.Loading()
            _receiveNewOrderLiveData.value = normalSaleRepositoryImpl.submitOrderSale(
                name,
                gold_type_id,
                gold_price,
                total_gold_weight_ywae,
                est_unit_wastage_ywae,
                qty,
                gem_value,
                maintenance_cost,
                date_of_delivery,
                remark,
                user_id,
                paid_amount,
                reduced_cost,
                localDatabase.getStockFromHomeSessionKey()?.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                old_stock_calc_type.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                oldStockSampleListId
            )
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


    private val _goldTypePriceLiveData = MutableLiveData<Resource<List<GoldTypePriceDto>>>()
    val goldTypePriceLiveData: LiveData<Resource<List<GoldTypePriceDto>>>
        get() = _goldTypePriceLiveData

    fun getGoldTypePrice() {
        _goldTypePriceLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _goldTypePriceLiveData.value = goldFromHomeRepositoryImpl.getGoldType(null)
        }
    }
    fun getTotalCVoucherBuyingPrice():String{
        return localDatabase.getTotalBVoucherBuyingPriceForStockFromHome().orEmpty()
    }
    fun getTotalGoldWeightYwae():String{
        return localDatabase.getGoldWeightYwaeForStockFromHome()?:"0"
    }
    fun getCustomerId():String{
        return localDatabase.getAccessCustomerId().orEmpty()
    }
    init {
        getGoldTypePrice()
    }
}