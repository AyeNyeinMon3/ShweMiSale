package com.example.shwemisale.screen.sellModule.goldBlockSale

import androidx.lifecycle.*
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.pureGoldSale.PureGoldListDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class AkoukSellViewModel @Inject constructor(
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase,
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val appDatabase: AppDatabase
) : ViewModel() {
    var goldPrice = ""


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

    fun getPureGoldSalesItems() {
        viewModelScope.launch {
            _getPureGoldItemsLiveData.value = Resource.Loading()
            _getPureGoldItemsLiveData.value = normalSaleRepositoryImpl.getPureGoldItems(
                localDatabase.getPureGoldSessionKey().orEmpty()
            )
        }
    }

    private val _updatePureGoldSaleItemsLiveData = MutableLiveData<Resource<String>>()
    val updatePureGoldSaleItemsLiveData: LiveData<Resource<String>>
        get() = _updatePureGoldSaleItemsLiveData

    fun updatePureGoldSalesItems(
        item: PureGoldListDomain,
    ) {
        viewModelScope.launch {
            val itemList = _getPureGoldItemsLiveData.value?.data.orEmpty()
            _updatePureGoldSaleItemsLiveData.value = Resource.Loading()
            val gold_weight_ywae = mutableListOf<MultipartBody.Part>()
            val maintenance_cost = mutableListOf<MultipartBody.Part>()
            val threading_fees = mutableListOf<MultipartBody.Part>()
            val type = mutableListOf<MultipartBody.Part>()
            val wastage_ywae = mutableListOf<MultipartBody.Part>()
                if (itemList.isNullOrEmpty().not()) {
                    repeat(itemList.size) {
                        if (itemList[it].id == item.id){
                            gold_weight_ywae.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][gold_weight_ywae]",
                                    item.gold_weight_ywae.toString()
                                )
                            )
                            maintenance_cost.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][maintenance_cost]",
                                    item.maintenance_cost.toString()
                                )
                            )
                            threading_fees.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][threading_fees]",
                                    item.threading_fees.toString()
                                )
                            )
                            type.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][type]",
                                    item.type.toString()
                                )
                            )
                            wastage_ywae.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][wastage_ywae]",
                                    item.wastage_ywae.toString()
                                )
                            )
                        }else{
                            gold_weight_ywae.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][gold_weight_ywae]",
                                    itemList[it].gold_weight_ywae.toString()
                                )
                            )
                            maintenance_cost.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][maintenance_cost]",
                                    itemList[it].maintenance_cost.toString()
                                )
                            )
                            threading_fees.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][threading_fees]",
                                    itemList[it].threading_fees.toString()
                                )
                            )
                            type.add(
                                MultipartBody.Part.createFormData(
                                    "items[$it][type]",
                                    itemList[it].type.toString()
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

                }

            _updatePureGoldSaleItemsLiveData.value = normalSaleRepositoryImpl.updatePureGoldItems(
                gold_weight_ywae,
                maintenance_cost,
                threading_fees,
                type,
                wastage_ywae,
                localDatabase.getPureGoldSessionKey().orEmpty()
            )

        }
    }

    private val _deletePureGoldSaleItemsLiveData = MutableLiveData<Resource<String>>()
    val deletePureGoldSaleItemsLiveData: LiveData<Resource<String>>
        get() = _deletePureGoldSaleItemsLiveData

    fun deletePureGoldSalesItems(
        item: PureGoldListDomain

    ) {
        viewModelScope.launch {
            _deletePureGoldSaleItemsLiveData.value = Resource.Loading()
            val updatedList = _getPureGoldItemsLiveData.value?.data?.filter { it != item }
            val gold_weight_ywae = mutableListOf<MultipartBody.Part>()
            val maintenance_cost = mutableListOf<MultipartBody.Part>()
            val threading_fees = mutableListOf<MultipartBody.Part>()
            val type = mutableListOf<MultipartBody.Part>()
            val wastage_ywae = mutableListOf<MultipartBody.Part>()
            if (updatedList != null) {
                if (updatedList.isNotEmpty()) {
                    repeat(updatedList.size) {
                        gold_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][gold_weight_ywae]",
                                updatedList[it].gold_weight_ywae.toString()
                            )
                        )
                        maintenance_cost.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][maintenance_cost]",
                                updatedList[it].maintenance_cost.toString()
                            )
                        )
                        threading_fees.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][threading_fees]",
                                updatedList[it].threading_fees.toString()
                            )
                        )
                        type.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][type]",
                                updatedList[it].type.toString()
                            )
                        )
                        wastage_ywae.add(
                            MultipartBody.Part.createFormData(
                                "items[$it][wastage_ywae]",
                                updatedList[it].wastage_ywae.toString()
                            )
                        )
                    }
                    _deletePureGoldSaleItemsLiveData.value =
                        normalSaleRepositoryImpl.updatePureGoldItems(
                            gold_weight_ywae,
                            maintenance_cost,
                            threading_fees,
                            type,
                            wastage_ywae,
                            localDatabase.getPureGoldSessionKey().orEmpty()
                        )
                } else {
                    _deletePureGoldSaleItemsLiveData.value =
                        normalSaleRepositoryImpl.updatePureGoldItems(
                            null,
                            null,
                            null,
                            null,
                            null,
                            localDatabase.getPureGoldSessionKey().orEmpty()
                        )
                }
            }


        }
    }

    private val _createPureGoldItemLiveData = MutableLiveData<Resource<String>>()
    val createPureGoldItemLiveData: LiveData<Resource<String>>
        get() = _createPureGoldItemLiveData

    fun createPureGoldSaleItem(
        gold_weight_ywae: String,
        maintenance_cost: String,
        threading_fees: String,
        type: String,
        wastage_ywae: String,
    ) {
        viewModelScope.launch {
            _createPureGoldItemLiveData.value = Resource.Loading()
            _createPureGoldItemLiveData.value = normalSaleRepositoryImpl.createPureGoldItems(
                gold_weight_ywae,
                maintenance_cost,
                threading_fees,
                type,
                wastage_ywae,
                localDatabase.getPureGoldSessionKey()
            )
        }
    }


    fun getCustomerId(): String {
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

        ) {
        viewModelScope.launch {
            _submitGoldBlockSell.value = Resource.Loading()
            _submitGoldBlockSell.value = normalSaleRepositoryImpl.submitPureGoldSale(
                localDatabase.getPureGoldSessionKey().orEmpty(),
                gold_price,
                user_id,
                paid_amount,
                reduced_cost,
                localDatabase.getStockFromHomeSessionKey().orEmpty()
                    .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }
    }

    fun getTotalCVoucherBuyingPrice(): String {
        return localDatabase.getTotalCVoucherBuyingPriceForStockFromHome().orEmpty()
    }
}