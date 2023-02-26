package com.example.shwemisale.screen.sellModule.generalSale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.example.shwemisale.data_layers.dto.GeneralSaleDto
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.repositoryImpl.ProductRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class GeneralSaleViewModel @Inject constructor(
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl
) : ViewModel() {
    private val _submitGeneralSaleLiveData = MutableLiveData<Resource<String>>()
    val submitGeneralSaleLiveData: LiveData<Resource<String>>
        get() = _submitGeneralSaleLiveData

    private val _generalSalesItemsLiveData = MutableLiveData<Resource<List<GeneralSaleDto>>>()
    val generalSalesItemsLiveData: LiveData<Resource<List<GeneralSaleDto>>>
        get() = _generalSalesItemsLiveData


    fun submitGeneralSale(
        itemsGeneralSaleItemId: List<MultipartBody.Part>?,
        itemsQty: List<MultipartBody.Part>?,
        itemsGoldWeightYwae: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
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
            _submitGeneralSaleLiveData.value = Resource.Loading()
            _submitGeneralSaleLiveData.value = normalSaleRepositoryImpl.submitGeneralSale(
                itemsGeneralSaleItemId, itemsQty, itemsGoldWeightYwae, itemsWastageYwae, itemsMaintenanceCost, user_id, paid_amount, reduced_cost, old_stocks_nameList, oldStockImageIds, oldStockImageFile, oldStockCondition, oldStockGoldGemWeightY, oldStockGemWeightY, oldStockImpurityWeightY, oldStockGoldWeightY, oldStockWastageWeightY, oldStockRebuyPrice, oldStockGQinCarat, oldStockMaintenance_cost, oldStockGemValue, oldStockPTAndClipCost, oldStockCalculatedBuyingValue, oldStockPriceForPawn, oldStockCalculatedForPawn, oldStockABuyingPrice, oldStockb_voucher_buying_value, oldStockc_voucher_buying_price, oldStockDGoldWeightY, oldStockEPriceFromNewVoucher, oldStockFVoucherShownGoldWeightY
            )
        }
    }

    fun getGeneralSaleItems(){
        viewModelScope.launch {
            _generalSalesItemsLiveData.value = Resource.Loading()
            _generalSalesItemsLiveData.value = normalSaleRepositoryImpl.getGeneralSalesItems()
        }
    }
}