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
import okhttp3.MultipartBody
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

        old_stocks_nameList: List<MultipartBody.Part>?,
        oldStockImageIds: List<MultipartBody.Part>?,
        oldStockImageFile: List<MultipartBody.Part>?,
        oldStockCondition: List<MultipartBody.Part>?,
        old_stock_qty: List<MultipartBody.Part>?,
        old_stock_size: List<MultipartBody.Part>?,
        oldStockGemWeightY: List<MultipartBody.Part>?,

        oldStockGoldGemWeightY: List<MultipartBody.Part>?,

        oldStockImpurityWeightY: List<MultipartBody.Part>?,

        oldStockGoldWeightY: List<MultipartBody.Part>?,

        oldStockWastageWeightY: List<MultipartBody.Part>?,

        oldStockRebuyPrice: List<MultipartBody.Part>?,
        oldStockGQinCarat: List<MultipartBody.Part>?,
        oldStockMaintenance_cost: List<MultipartBody.Part>?,
        oldStockGemValue: List<MultipartBody.Part>?,
        oldStockGemDetailQty: List<MultipartBody.Part>?,
        oldStockGemDetailGm: List<MultipartBody.Part>?,
        oldStockGemDetailYwae: List<MultipartBody.Part>?,
        oldStockPTAndClipCost: List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue: List<MultipartBody.Part>?,
        oldStockPriceForPawn: List<MultipartBody.Part>?,
        oldStockCalculatedForPawn: List<MultipartBody.Part>?,

        oldStockABuyingPrice: List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value: List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price: List<MultipartBody.Part>?,

        oldStockDGoldWeightY: List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher: List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,
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

                old_stocks_nameList,
                oldStockGemDetailQty,
                oldStockGemDetailGm,
                oldStockGemDetailYwae,
                oldStockImageIds,
                oldStockImageFile,
                oldStockCondition,
                old_stock_qty,
                old_stock_size,
                oldStockGemWeightY,
                oldStockGoldGemWeightY,
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
                oldStockFVoucherShownGoldWeightY,
                oldStockSampleListId
            )
        }
    }

    var stockFromHomeList = emptyList<StockFromHomeInfoUiModel>()
    var stockFromHomeFinalInfo: StockFromHomeFinalInfo? = null
    var stockFromHomeListInRoom =
        appDatabase.stockFromHomeInfoDao.getStockFromHomeInfo().map { it.map { it.asUiModel() } }
    var stockFromHomeFinalInfoInRoom =
        appDatabase.stockFromHomeFinalInfoDao.getStockFromHomeFinalInfo()

    private val _goldTypePriceLiveData = MutableLiveData<Resource<List<GoldTypePriceDto>>>()
    val goldTypePriceLiveData: LiveData<Resource<List<GoldTypePriceDto>>>
        get() = _goldTypePriceLiveData

    fun getGoldTypePrice() {
        _goldTypePriceLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _goldTypePriceLiveData.value = goldFromHomeRepositoryImpl.getGoldType(null)
        }
    }
    fun getCustomerId():String{
        return localDatabase.getAccessCustomerId().orEmpty()
    }
    init {
        getGoldTypePrice()
    }
}