package com.example.shwemisale.screen.sellModule.goldBlockSale

import androidx.lifecycle.*
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.example.shwemisale.data_layers.domain.pureGoldSale.PureGoldListDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.repositoryImpl.ProductRepoImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo
import com.example.shwemisale.room_database.entity.asUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AkoukSellViewModel @Inject constructor(
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase,
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val appDatabase: AppDatabase
) : ViewModel() {
    var goldPrice =""
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
            _goldTypePriceLiveData.value = goldFromHomeRepositoryImpl.getGoldType("1")
        }
    }
    init {
        getGoldTypePrice()
    }
    private val _getPureGoldItemsLiveData = MutableLiveData<Resource<List<PureGoldListDomain>>>()
    val getPureGoldItemLiveData: LiveData<Resource<List<PureGoldListDomain>>>
        get() = _getPureGoldItemsLiveData

    fun getPureGoldSalesItems(){
        viewModelScope.launch {
            _getPureGoldItemsLiveData.value = Resource.Loading()
            _getPureGoldItemsLiveData.value = normalSaleRepositoryImpl.getPureGoldItems(
                localDatabase.getSessionKey().orEmpty()
            )
        }
    }

    private val _updatePureGoldSaleItemsLiveData = MutableLiveData<Resource<String>>()
    val updatePureGoldSaleItemsLiveData: LiveData<Resource<String>>
        get() = _updatePureGoldSaleItemsLiveData
    fun updatePureGoldSalesItems(
        gold_weight_ywae: List<MultipartBody.Part>?,
        maintenance_cost: List<MultipartBody.Part>?,
        threading_fees: List<MultipartBody.Part>?,
        type: List<MultipartBody.Part>?,
        wastage_ywae: List<MultipartBody.Part>?,

    ){
        viewModelScope.launch{
            _updatePureGoldSaleItemsLiveData.value = Resource.Loading()
            _updatePureGoldSaleItemsLiveData.value = normalSaleRepositoryImpl.updatePureGoldItems(
                gold_weight_ywae,maintenance_cost,threading_fees,type,wastage_ywae,localDatabase.getSessionKey().orEmpty()
            )

        }
    }

    private val _createPureGoldItemLiveData = MutableLiveData<Resource<String>>()
    val createPureGoldItemLiveData: LiveData<Resource<String>>
        get() = _createPureGoldItemLiveData
    fun createPureGoldSaleItem(
        gold_weight_ywae:String,
        maintenance_cost:String,
        threading_fees:String,
        type:String,
        wastage_ywae:String,
        ){
        viewModelScope.launch{
            _createPureGoldItemLiveData.value = Resource.Loading()
            _createPureGoldItemLiveData.value = normalSaleRepositoryImpl.createPureGoldItems(
                gold_weight_ywae,maintenance_cost,threading_fees,type,wastage_ywae,localDatabase.getSessionKey()
            )
        }
    }


    fun getCustomerId():String{
        return localDatabase.getAccessCustomerId().orEmpty()
    }

    private val _submitGoldBlockSell = MutableLiveData<Resource<String>>()
    val submitGoldBlockSell: LiveData<Resource<String>>
        get() = _submitGoldBlockSell

    fun submitPureGoldSale(
        gold_price: String,
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

    ) {
        viewModelScope.launch {
            _submitGoldBlockSell.value = Resource.Loading()
            _submitGoldBlockSell.value = normalSaleRepositoryImpl.submitPureGoldSale(
                localDatabase.getSessionKey().orEmpty(),
                gold_price,
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
            )
        }
    }
}