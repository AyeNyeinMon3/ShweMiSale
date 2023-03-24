package com.example.shwemisale.screen.sellModule.recieveNewOrder

import androidx.lifecycle.*
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.example.shwemisale.data_layers.domain.sample.SampleDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo
import com.example.shwemisale.room_database.entity.asUiModel
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
    private val localDatabase: LocalDatabase
) : ViewModel() {

    var takenSamples = listOf<SampleDomain>()
    var samplesFromRoom = normalSaleRepositoryImpl.getSamplesFromRoom()

    private val _receiveNewOrderLiveData = MutableLiveData<Resource<String>>()
    val receiveNewOrderLiveData: LiveData<Resource<String>>
        get() = _receiveNewOrderLiveData

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
                localDatabase.getStockFromHomeSessionKey().orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                oldStockSampleListId
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
    fun getTotalCVoucherBuyingPrice():String{
        return localDatabase.getTotalCVoucherBuyingPriceForStockFromHome().orEmpty()
    }
    fun getTotalGoldWeightYwae():String{
        return localDatabase.getGoldWeightYwaeForStockFromHome().orEmpty()
    }
    fun getCustomerId():String{
        return localDatabase.getAccessCustomerId().orEmpty()
    }
    init {
        getGoldTypePrice()
    }
}