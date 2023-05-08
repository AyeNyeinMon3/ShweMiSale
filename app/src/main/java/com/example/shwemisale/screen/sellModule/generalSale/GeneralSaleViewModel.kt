package com.example.shwemisale.screen.sellModule.generalSale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.generalSale.GeneralSaleListDomain
import com.example.shwemisale.data_layers.dto.GeneralSaleDto
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class GeneralSaleViewModel @Inject constructor(
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase,
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl
) : ViewModel() {
    var goldPrice = ""
    private val _createGeneralItemLiveData = MutableLiveData<Resource<String>>()
    val createGeneralItemLiveData: LiveData<Resource<String>>
        get() = _createGeneralItemLiveData
    private val _updateGeneralSaleItemsLiveData = SingleLiveEvent<Resource<String>>()
    val updateGeneralSaleItemsLiveData: SingleLiveEvent<Resource<String>>
        get() = _updateGeneralSaleItemsLiveData
    private val _deleteGeneralSaleItemsLiveData = SingleLiveEvent<Resource<String>>()
    val deleteGeneralSaleItemsLiveData: SingleLiveEvent<Resource<String>>
        get() = _deleteGeneralSaleItemsLiveData
    private val _goldTypePriceLiveData = MutableLiveData<Resource<List<GoldTypePriceDto>>>()
    val goldTypePriceLiveData: LiveData<Resource<List<GoldTypePriceDto>>>
        get() = _goldTypePriceLiveData

    fun getGoldTypePrice() {
        _goldTypePriceLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _goldTypePriceLiveData.value = goldFromHomeRepositoryImpl.getGoldType(null)
        }
    }

    var generalSaleItemListForMap:List<GeneralSaleDto>? = null
    private val _generalSaleReasonLiveData = MutableLiveData<Resource<List<GeneralSaleDto>>>()
    val generalSaleReasonLiveData: LiveData<Resource<List<GeneralSaleDto>>>
        get() = _generalSaleReasonLiveData

    fun getGeneralSaleReasons() {
        _generalSaleReasonLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _generalSaleReasonLiveData.value = normalSaleRepositoryImpl.getGeneralSalesItems()
        }
    }

    init {
        getGoldTypePrice()
    }
    private val _submitGeneralSaleLiveData = MutableLiveData<Resource<String>>()
    val submitGeneralSaleLiveData: LiveData<Resource<String>>
        get() = _submitGeneralSaleLiveData

    private val _generalSalesItemsLiveData = MutableLiveData<Resource<List<GeneralSaleListDomain>>>()
    val generalSalesItemsLiveData: LiveData<Resource<List<GeneralSaleListDomain>>>
        get() = _generalSalesItemsLiveData


    fun submitGeneralSale(
        paid_amount: String,
        reduced_cost: String,

    ) {
        viewModelScope.launch {
            _submitGeneralSaleLiveData.value = Resource.Loading()
            _submitGeneralSaleLiveData.value = normalSaleRepositoryImpl.submitGeneralSale(
                localDatabase.getGeneralSaleSessionKey().orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                localDatabase.getAccessCustomerId().orEmpty(),
                paid_amount,
                reduced_cost,
                localDatabase.getStockFromHomeSessionKey().orEmpty().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }
    }

    fun getGeneralSaleItems() {
        viewModelScope.launch {
            _generalSalesItemsLiveData.value = Resource.Loading()
            _generalSalesItemsLiveData.value = normalSaleRepositoryImpl.getGeneralSaleItems(localDatabase.getGeneralSaleSessionKey().orEmpty())
        }
    }


    fun updateGeneralSalesItems(
        item:GeneralSaleListDomain
        ){
        viewModelScope.launch {
            _updateGeneralSaleItemsLiveData.value = Resource.Loading()
            val itemList = _generalSalesItemsLiveData.value?.data?.filter { it != item }
            val general_sale_item_id = mutableListOf<MultipartBody.Part>()
            val gold_weight_gm = mutableListOf<MultipartBody.Part>()
            val maintenance_cost = mutableListOf<MultipartBody.Part>()
            val qty = mutableListOf<MultipartBody.Part>()
            val wastage_ywae = mutableListOf<MultipartBody.Part>()
            if (itemList != null) {
                if (itemList.isNotEmpty()) {
                    repeat(itemList.size) {
                        if (itemList[it].id == item.id){
                            general_sale_item_id.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][general_sale_item_id]",
                                    item.general_sale_item_id.toString()
                                )
                            )
                            gold_weight_gm.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][gold_weight_gm]",
                                    item.gold_weight_gm.toString()
                                )
                            )
                            maintenance_cost.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][maintenance_cost]",
                                    item.maintenance_cost.toString()
                                )
                            )
                            qty.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][qty]",
                                    item.qty.toString()
                                )
                            )
                            wastage_ywae.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][wastage_ywae]",
                                    item.wastage_ywae.toString()
                                )
                            )
                        }else{
                            general_sale_item_id.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][general_sale_item_id]",
                                    itemList[it].general_sale_item_id.toString()
                                )
                            )
                            gold_weight_gm.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][gold_weight_gm]",
                                    itemList[it].gold_weight_gm.toString()
                                )
                            )
                            maintenance_cost.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][maintenance_cost]",
                                    itemList[it].maintenance_cost.toString()
                                )
                            )
                            qty.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][qty]",
                                    itemList[it].qty.toString()
                                )
                            )
                            wastage_ywae.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][wastage_ywae]",
                                    itemList[it].wastage_ywae.toString()
                                )
                            )
                        }

                    }
                    _updateGeneralSaleItemsLiveData.value =
                        normalSaleRepositoryImpl.updateGeneralSaleItems(
                            general_sale_item_id,
                            gold_weight_gm,
                            maintenance_cost,
                            qty,
                            wastage_ywae,
                            localDatabase.getGeneralSaleSessionKey().orEmpty()
                        )
                }


            }
        }
    }


    fun deleteGeneralSaleItem(
        item: GeneralSaleListDomain
    ){
        viewModelScope.launch {
            _deleteGeneralSaleItemsLiveData.value = Resource.Loading()
            val updatedList = _generalSalesItemsLiveData.value?.data?.filter { it != item }
            val general_sale_item_id = mutableListOf<MultipartBody.Part>()
            val gold_weight_gm = mutableListOf<MultipartBody.Part>()
            val maintenance_cost = mutableListOf<MultipartBody.Part>()
            val qty = mutableListOf<MultipartBody.Part>()
            val wastage_ywae = mutableListOf<MultipartBody.Part>()
            if (updatedList != null) {
                if (updatedList.isNotEmpty()) {
                    repeat(updatedList.size) {
                        general_sale_item_id.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][general_sale_item_id]",
                                updatedList[it].general_sale_item_id.toString()
                            )
                        )
                        gold_weight_gm.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][gold_weight_gm]",
                                updatedList[it].gold_weight_gm.toString()
                            )
                        )
                        maintenance_cost.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][maintenance_cost]",
                                updatedList[it].maintenance_cost.toString()
                            )
                        )
                        qty.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][qty]",
                                updatedList[it].qty.toString()
                            )
                        )
                        wastage_ywae.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][wastage_ywae]",
                                updatedList[it].wastage_ywae.toString()
                            )
                        )
                    }
                    _deleteGeneralSaleItemsLiveData.value =
                        normalSaleRepositoryImpl.updateGeneralSaleItems(
                            general_sale_item_id,
                            gold_weight_gm,
                            maintenance_cost,
                            qty,
                            wastage_ywae,
                            localDatabase.getGeneralSaleSessionKey().orEmpty()
                        )
                } else {
                    localDatabase.removeGeneralSaleSessionKey()
                    getGeneralSaleItems()
                }
            }


        }
    }


    fun createGeneralSaleItem(
        general_sale_item_id: String,
        gold_weight_gm: String,
        maintenance_cost: String,
        qty: String,
        wastage_ywae: String
    ){
        viewModelScope.launch{
            _createGeneralItemLiveData.value = Resource.Loading()
            _createGeneralItemLiveData.value = normalSaleRepositoryImpl.createGeneralSaleItems(
                general_sale_item_id, gold_weight_gm, maintenance_cost, qty, wastage_ywae
            )
        }
    }
    fun getTotalCVoucherBuyingPrice(): String {
        return localDatabase.getTotalCVoucherBuyingPriceForStockFromHome().orEmpty()
    }
}