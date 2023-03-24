package com.example.shwemisale.screen.sellModule.generalSale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.example.shwemisale.data_layers.dto.GeneralSaleDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.repositoryImpl.ProductRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class GeneralSaleViewModel @Inject constructor(
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase
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

    ) {
        viewModelScope.launch {
            _submitGeneralSaleLiveData.value = Resource.Loading()
            _submitGeneralSaleLiveData.value = normalSaleRepositoryImpl.submitGeneralSale(
                itemsGeneralSaleItemId,
                itemsQty,
                itemsGoldWeightYwae,
                itemsWastageYwae,
                itemsMaintenanceCost,
                user_id,
                paid_amount,
                reduced_cost,
                localDatabase.getStockFromHomeSessionKey().orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }
    }

    fun getGeneralSaleItems() {
        viewModelScope.launch {
            _generalSalesItemsLiveData.value = Resource.Loading()
            _generalSalesItemsLiveData.value = normalSaleRepositoryImpl.getGeneralSalesItems()
        }
    }
}