package com.shwemigoldshop.shwemisale.screen.sellModule.normalSaleScanStock.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.product.ProductSizeAndReasonDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.shwemigoldshop.shwemisale.data_layers.ui_models.product.ProductInfoUiModel
import com.shwemigoldshop.shwemisale.repositoryImpl.ProductRepoImpl
import com.shwemigoldshop.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanStockDetailViewModel @Inject constructor(
    private val productRepoImpl: ProductRepoImpl
) : ViewModel() {
    val productInfoList = mutableListOf<ProductInfoUiModel>()
    private val _goldTypePriceLiveData = SingleLiveEvent<Resource<List<GoldTypePriceDto>>>()
    val goldTypePriceLiveData: SingleLiveEvent<Resource<List<GoldTypePriceDto>>>
        get() = _goldTypePriceLiveData
    private val _productInfoLiveData = MutableLiveData<List<ProductInfoUiModel>>()
    val productInfoLiveData: LiveData<List<ProductInfoUiModel>>
        get() = _productInfoLiveData

    fun addProduct(item:ProductInfoUiModel){
        if (productInfoList.map { it.id }.contains(item.id)) productInfoList.remove(productInfoList.find { it.id == item.id })
        productInfoList.add(item)
        _productInfoLiveData.value = productInfoList.distinctBy { it.id }
    }
    fun deleteItem(id:String){
        productInfoList.remove(productInfoList.find { it.id == id })
        _productInfoLiveData.value = productInfoList.distinctBy { it.id }
    }

    fun removeTemp(id:String){
        viewModelScope.launch {
            productRepoImpl.removeTemp(id)
        }
    }


    private val _productInfoScanLiveData = SingleLiveEvent<Resource<ProductInfoDomain>>()
    val productInfoScanLiveData: SingleLiveEvent<Resource<ProductInfoDomain>>
        get() = _productInfoScanLiveData


    private val _productIdLiveData = MutableLiveData<Resource<String>>()
    val productIdLiveData: LiveData<Resource<String>>
        get() = _productIdLiveData

    fun getProductInfo(productId: String) {
        viewModelScope.launch {
            _productInfoScanLiveData.value = Resource.Loading()
            _productInfoScanLiveData.value = productRepoImpl.getProductInfo(productId)
        }
    }

    fun getProductId(productCode: String) {
        viewModelScope.launch {
            _productIdLiveData.value = Resource.Loading()
            _productIdLiveData.value = productRepoImpl.getProductId(productCode)
        }
    }


    private val _productSizeAndReasonLiveData =
        MutableLiveData<Resource<ProductSizeAndReasonDomain>>()
    val productSizeAndReasonLiveData: LiveData<Resource<ProductSizeAndReasonDomain>>
        get() = _productSizeAndReasonLiveData

    fun getProductSizeAndReason(productId: String) {
        viewModelScope.launch {
            _productSizeAndReasonLiveData.value = Resource.Loading()
            _productSizeAndReasonLiveData.value = productRepoImpl.getProductSizeAndReason(productId)
        }
    }

    private val _updateProductInfoLiveData = SingleLiveEvent<Resource<String>>()
    val updateProductInfoLiveData: SingleLiveEvent<Resource<String>>
        get() = _updateProductInfoLiveData

    fun updateProductInfo(
        productId: String,
        gold_and_gem_weight_gm: String?,
        gem_weight_ywae: String?,
        wastage_ywae: String?,
        gem_value: String?,
        promotion_discount: String?,
        jewellery_type_size_id: String?,
        edit_reason_id: String?,
        pt_and_clip_cost: String?,
        maintenance_cost: String?,
        general_sale_item_id: String?,
        new_clip_wt_gm: String?,
        old_clip_wt_gm:String?,
        edited_gold_price:String?,
    ) {
        productInfoList.find { it.id == productId }?.gold_and_gem_weight_gm

        viewModelScope.launch {
            _updateProductInfoLiveData.value = Resource.Loading()
            _updateProductInfoLiveData.value = productRepoImpl.updateProductInfo(
                productId,
                gold_and_gem_weight_gm,
                gem_weight_ywae,
                wastage_ywae,
                gem_value,
                promotion_discount,
                jewellery_type_size_id,
                edit_reason_id,
                pt_and_clip_cost,
                maintenance_cost,
                general_sale_item_id,
                new_clip_wt_gm,
                old_clip_wt_gm,
                edited_gold_price
            )
        }
    }

    fun getGoldTypePrice(goldTypeId:String){
        _goldTypePriceLiveData.value= Resource.Loading()
        viewModelScope.launch {
            _goldTypePriceLiveData.value = productRepoImpl.getGoldType(goldTypeId)
        }
    }
}