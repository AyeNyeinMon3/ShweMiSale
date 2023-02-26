package com.example.shwemisale.screen.sellModule.recieveNewOrder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ReceiveNewOrderViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl
):ViewModel() {
    private val _receiveNewOrderLiveData = MutableLiveData<Resource<String>>()
    val receiveNewOrderLiveData: LiveData<Resource<String>>
        get() = _receiveNewOrderLiveData

    fun getProductInfo(
        name: String,
        gold_type_id: String,
        gold_price: String,
        total_gold_weight_ywae: String,
        est_unit_wastage_ywae: String,
        qty: String,
        gem_value: String,
        maintenance_cost: String,
        date_of_delivery: String,
        is_delivered: String,
        remark: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        itemsGeneralSaleItemId: List<MultipartBody.Part>?,
        itemsQty: List<MultipartBody.Part>?,
        itemsGoldWeightGm: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,
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
        oldStockFVoucherShownGoldWeightY: List<MultipartBody.Part>?,
        oldStockSampleListId: List<MultipartBody.Part>?
    ) {
        viewModelScope.launch {
            _receiveNewOrderLiveData.value = Resource.Loading()
            _receiveNewOrderLiveData.value = normalSaleRepositoryImpl.submitOrderSale(name, gold_type_id, gold_price, total_gold_weight_ywae, est_unit_wastage_ywae, qty, gem_value, maintenance_cost, date_of_delivery, is_delivered, remark, user_id, paid_amount, reduced_cost, itemsGeneralSaleItemId, itemsQty, itemsGoldWeightGm, itemsWastageYwae, itemsMaintenanceCost, old_stocks_nameList, oldStockImageIds, oldStockImageFile, oldStockCondition, oldStockGoldGemWeightY, oldStockGemWeightY, oldStockImpurityWeightY, oldStockGoldWeightY, oldStockWastageWeightY, oldStockRebuyPrice, oldStockGQinCarat, oldStockMaintenance_cost, oldStockGemValue, oldStockPTAndClipCost, oldStockCalculatedBuyingValue, oldStockPriceForPawn, oldStockCalculatedForPawn, oldStockABuyingPrice, oldStockb_voucher_buying_value, oldStockc_voucher_buying_price, oldStockDGoldWeightY, oldStockEPriceFromNewVoucher, oldStockFVoucherShownGoldWeightY, oldStockSampleListId)
        }
    }
}