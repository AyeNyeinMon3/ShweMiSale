package com.example.shwemisale.screen.sellModule.normalSaleScanStock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.example.shwemisale.data_layers.domain.product.ProductSizeAndReasonDomain
import com.example.shwemisale.data_layers.ui_models.product.ProductInfoUiModel
import com.example.shwemisale.repositoryImpl.ProductRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanStockViewModel @Inject constructor(
    private val productRepoImpl: ProductRepoImpl
) : ViewModel() {
    val productInfoList = mutableListOf<ProductInfoUiModel>()

    private val _productInfoLiveData = MutableLiveData<Resource<ProductInfoDomain>>()
    val productInfoLiveData: LiveData<Resource<ProductInfoDomain>>
        get() = _productInfoLiveData

    fun getProductInfo(productId: String) {
        viewModelScope.launch {
            _productInfoLiveData.value = Resource.Loading()
            _productInfoLiveData.value = productRepoImpl.getProductInfo(productId)
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

    private val _updateProductInfoLiveData = MutableLiveData<Resource<String>>()
    val updateProductInfoLiveData: LiveData<Resource<String>>
        get() = _updateProductInfoLiveData

    fun updateProductInfo(
        productId: String,
        gold_and_gem_weight_gm: String?,
        gem_weight_ywae: String?,
        gem_value: String?,
        promotion_discount: String?,
        jewellery_type_size_id: String?,
        edit_reason_id: String?,
        pt_and_clip_cost: String?,
        general_sale_item_id: String?,
        new_clip_wt_gm: String?
    ) {
        viewModelScope.launch {
            _updateProductInfoLiveData.value = Resource.Loading()
            _updateProductInfoLiveData.value = productRepoImpl.updateProductInfo(
                productId,
                gold_and_gem_weight_gm,
                gem_weight_ywae,
                gem_value,
                promotion_discount,
                jewellery_type_size_id,
                edit_reason_id,
                pt_and_clip_cost,
                general_sale_item_id,
                new_clip_wt_gm
            )
        }
    }
}