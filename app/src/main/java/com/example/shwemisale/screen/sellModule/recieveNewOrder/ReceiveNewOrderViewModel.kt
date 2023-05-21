package com.example.shwemisale.screen.sellModule.recieveNewOrder

import androidx.lifecycle.*
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.example.shwemisale.data_layers.domain.sample.SampleDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo
import com.example.shwemisale.room_database.entity.asEntity
import com.example.shwemisale.room_database.entity.asUiModel
import com.example.shwemisale.util.SingleLiveEvent
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
//    var goldFromHomeWithKpy = "0"
//    var goldFromHomeWithValue = "0"
    var goldPrice = 0
    var takenSamples = listOf<SampleDomain>()
    var samplesFromRoom = normalSaleRepositoryImpl.getSamplesFromRoom()
    private val _stockFromHomeInfoLiveData = SingleLiveEvent<Resource<List<StockFromHomeDomain>>>()
    val stockFromHomeInfoLiveData: SingleLiveEvent<Resource<List<StockFromHomeDomain>>>
        get() = _stockFromHomeInfoLiveData
    private val _updateStockFromHomeInfoLiveData =
        SingleLiveEvent<Resource<String>>()
    val updateStockFromHomeInfoLiveData: SingleLiveEvent<Resource<String>>
        get() = _updateStockFromHomeInfoLiveData
    private val _receiveNewOrderLiveData = MutableLiveData<Resource<String>>()
    val receiveNewOrderLiveData: LiveData<Resource<String>>
        get() = _receiveNewOrderLiveData
    fun addTotalGoldWeightYwaeToStockFromHome(ywae:String){
        localDatabase.saveGoldWeightYwaeForStockFromHome(ywae)
    }
    fun removeSampleFromRoom(sample:SampleDomain){
        viewModelScope.launch {
            appDatabase.sampleDao.deleteSamples(sample.localId.orEmpty())
            samplesFromRoom =  normalSaleRepositoryImpl.getSamplesFromRoom()
        }
    }
    fun getStockFromHomeList() {
        _stockFromHomeInfoLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _stockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.getStockFromHomeList(
                    localDatabase.getStockFromHomeSessionKey().orEmpty()
                )
        }
    }
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
    fun updateStockFromHome(
        e_price: String,
        itemList: List<StockFromHomeDomain>
    ) {
        viewModelScope.launch {
            var totalFYwae = 0.0
            val a_buying_price = mutableListOf<MultipartBody.Part>()
            val b_voucher_buying_value = mutableListOf<MultipartBody.Part>()
            val c_voucher_buying_price = mutableListOf<MultipartBody.Part>()
            val calculated_buying_value = mutableListOf<MultipartBody.Part>()
            val calculated_for_pawn = mutableListOf<MultipartBody.Part>()
            val d_gold_weight_ywae = mutableListOf<MultipartBody.Part>()
            val e_price_from_new_voucher = mutableListOf<MultipartBody.Part>()
            val f_voucher_shown_gold_weight_ywae = mutableListOf<MultipartBody.Part>()
            val gem_value = mutableListOf<MultipartBody.Part>()
            var gemQtyMultiPartList = mutableListOf<MultipartBody.Part?>()
            var gemWeightYwaeMultiPartList = mutableListOf<MultipartBody.Part?>()
            var gemWeightGmMultiPartList = mutableListOf<MultipartBody.Part?>()
            val gem_weight_ywae = mutableListOf<MultipartBody.Part>()
            val gold_gem_weight_ywae = mutableListOf<MultipartBody.Part>()
            val gold_weight_ywae = mutableListOf<MultipartBody.Part>()
            val gq_in_carat = mutableListOf<MultipartBody.Part>()
            val has_general_expenses = mutableListOf<MultipartBody.Part>()
            val imageId = mutableListOf<MultipartBody.Part>()
            val imageFile = mutableListOf<MultipartBody.Part>()
            val impurities_weight_ywae = mutableListOf<MultipartBody.Part>()
            val maintenance_cost = mutableListOf<MultipartBody.Part>()
            val price_for_pawn = mutableListOf<MultipartBody.Part>()
            val pt_and_clip_cost = mutableListOf<MultipartBody.Part>()
            val qty = mutableListOf<MultipartBody.Part>()
            val rebuy_price = mutableListOf<MultipartBody.Part>()
            val size = mutableListOf<MultipartBody.Part>()
            val stock_condition = mutableListOf<MultipartBody.Part>()
            val stock_name = mutableListOf<MultipartBody.Part>()
            val type = mutableListOf<MultipartBody.Part>()
            val wastage_ywae = mutableListOf<MultipartBody.Part>()
            val rebuy_price_vertical_option = mutableListOf<MultipartBody.Part>()
            val productIdList = mutableListOf<MultipartBody.Part>()
            val isEditable = mutableListOf<MultipartBody.Part>()
            val isChecked = mutableListOf<MultipartBody.Part>()
            repeat(itemList.size) {
                val gemQtyList =
                    itemList[it].gem_weight_details.orEmpty().map { it.gem_qty }
                val gemWeightYwaeList =
                    itemList[it].gem_weight_details.orEmpty()
                        .map { it.gem_weight_ywae_per_unit }
                val gemWeightGmList =
                    itemList[it].gem_weight_details.orEmpty()
                        .map { it.gem_weight_gm_per_unit }

                repeat(itemList[it].gem_weight_details.orEmpty().size) {gemWeightIndex->
                    gemQtyMultiPartList.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$gemWeightIndex][gem_qty]",
                            gemQtyList[gemWeightIndex]
                        )
                    )
                    gemWeightYwaeMultiPartList.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$gemWeightIndex][gem_weight_ywae_per_unit]",
                            gemWeightYwaeList[gemWeightIndex]
                        )
                    )
                    gemWeightGmMultiPartList.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$gemWeightIndex][gem_weight_gm_per_unit]",
                            gemWeightGmList[gemWeightIndex]
                        )
                    )
                }

                a_buying_price.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][a_buying_price]",
                        itemList[it].a_buying_price.toString()
                    )
                )

                b_voucher_buying_value.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][b_voucher_buying_value]",
                        itemList[it].b_voucher_buying_value.toString()
                    )
                )

                c_voucher_buying_price.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][c_voucher_buying_price]",
                        itemList[it].c_voucher_buying_price.toString()
                    )
                )

                calculated_buying_value.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][calculated_buying_value]",
                        itemList[it].calculated_buying_value.toString()
                    )
                )

                calculated_for_pawn.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][calculated_for_pawn]",
                        itemList[it].calculated_for_pawn.toString()
                    )
                )

                d_gold_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][d_gold_weight_ywae]",
                        itemList[it].d_gold_weight_ywae.toString()
                    )
                )

                e_price_from_new_voucher.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][e_price_from_new_voucher]",
                        e_price
                    )
                )
                var fywae =
                    (itemList[it].b_voucher_buying_value.let { if (it.isNullOrEmpty()) "0.0" else it }
                        .toDouble() / e_price.toDouble()) * 128

                totalFYwae += fywae

                f_voucher_shown_gold_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][f_voucher_shown_gold_weight_ywae]",
                        fywae.toString()
                    )
                )

                gem_value.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gem_value]",
                        itemList[it].gem_value.toString()
                    )
                )

                gem_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gem_weight_ywae]",
                        itemList[it].gem_weight_ywae.toString()
                    )
                )

                gold_gem_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gold_gem_weight_ywae]",
                        itemList[it].gold_gem_weight_ywae.toString()
                    )
                )
                gold_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gold_weight_ywae]",
                        itemList[it].gold_weight_ywae.toString()
                    )
                )
                gq_in_carat.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gq_in_carat]",
                        itemList[it].gq_in_carat.toString()
                    )
                )
                has_general_expenses.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][has_general_expenses]",
                        "1"
                    )
                )
                itemList[it].image?.id?.let { id ->
                    imageId.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][image][id]",
                            id
                        )
                    )
                }
//                        itemList[it].image?.url?.let {url->
//                            imageFile.add(
//                                MultipartBody.Part.createFormData(
//                                    "old_stocks[$it][imageId]",
//                                    url
//                                )
//                            )
//                        }


                maintenance_cost.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][maintenance_cost]",
                        itemList[it].maintenance_cost.toString()
                    )
                )
                price_for_pawn.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][price_for_pawn]",
                        itemList[it].price_for_pawn.toString()
                    )
                )
                pt_and_clip_cost.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][pt_and_clip_cost]",
                        itemList[it].pt_and_clip_cost.toString()
                    )
                )
                qty.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][qty]",
                        itemList[it].qty.toString()
                    )
                )

                rebuy_price.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][rebuy_price]",
                        itemList[it].rebuy_price
                    )
                )
                size.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][size]",
                        itemList[it].size.toString()
                    )
                )
                stock_condition.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][stock_condition]",
                        itemList[it].stock_condition.toString()
                    )
                )
                stock_name.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][stock_name]",
                        itemList[it].stock_name.toString()
                    )
                )
                type.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][type]",
                        itemList[it].type.toString()
                    )
                )

                //ထည့်ပေးအရော့= [(calculated ဆိုင်မှ၀ယ်ပေးငွေ- လက်ခ- ကျောက်စိန်ဖိုး- PT, ကလစ်ဖိုး)/ Rebuy price]KPY - Gold wt KPY
                val repurchasePrice =
                    itemList[it].rebuy_price.let { if (it.isEmpty()) 0 else it.toInt() }
                val maintainenceCost =
                    itemList[it].maintenance_cost.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                val gemValue =
                    itemList[it].gem_value.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                val ptClipValue =
                    itemList[it].pt_and_clip_cost.let { if (it.isNullOrEmpty()) 0 else it.toInt() }

                val goldWeightYwae =
                    itemList[it].gold_weight_ywae.let { if (it.isNullOrEmpty()) 0.0 else it.toDouble() }
                val wastageYwae =
                    (((repurchasePrice - maintainenceCost - gemValue - ptClipValue) / e_price.toInt()) * 128) - goldWeightYwae


//                if (wastageYwae < 0.0){
//                    val impuritiesTotalWeight = wastageYwae*(-1) + itemList[it].impurities_weight_ywae.let {
//                        if (it.isNullOrEmpty()) 0.0 else it.toDouble()
//                    }.toDouble()
//                    impurities_weight_ywae.add(
//                        MultipartBody.Part.createFormData(
//                            "old_stocks[$it][impurities_weight_ywae]",
//                           impuritiesTotalWeight.toString()
//                        )
//                    )
//
//                }else{
//
//                    wastage_ywae.add(
//                        MultipartBody.Part.createFormData(
//                            "old_stocks[$it][wastage_ywae]",
//                            wastageYwae.toString()
//                        )
//                    )
//                }
                wastage_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][wastage_ywae]",
                        itemList[it].wastage_ywae.toString()
                    )
                )
                impurities_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][impurities_weight_ywae]",
                        itemList[it].impurities_weight_ywae.toString()
                    )
                )

                rebuy_price_vertical_option.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][rebuy_price_vertical_option]",
                        itemList[it].rebuy_price_vertical_option.orEmpty()
                    )
                )
                repeat(itemList[it].productId.orEmpty().size) { index ->
                    productIdList.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][products][]",
                            itemList[it].productId.orEmpty()[index]
                        )
                    )
                }
                isEditable.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][is_editable]",
                        "1"
                    )
                )
                isChecked.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][is_checked]",
                        "0"
                    )
                )
            }

            addTotalGoldWeightYwaeToStockFromHome(totalFYwae.toString())
            _updateStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.updateStockFromHomeList(
                    a_buying_price = a_buying_price,
                    b_voucher_buying_value = b_voucher_buying_value,
                    c_voucher_buying_price = c_voucher_buying_price,
                    calculated_buying_value = calculated_buying_value,
                    calculated_for_pawn = calculated_for_pawn,
                    d_gold_weight_ywae = d_gold_weight_ywae,
                    e_price_from_new_voucher = e_price_from_new_voucher,
                    f_voucher_shown_gold_weight_ywae = f_voucher_shown_gold_weight_ywae,
                    gem_value = gem_value,
                    gem_weight_details_qty = gemQtyMultiPartList,
                    gem_weight_details_gm = gemWeightGmMultiPartList,
                    gem_weight_details_ywae = gemWeightYwaeMultiPartList,
                    gem_weight_ywae = gem_weight_ywae,
                    gold_weight_ywae = gold_weight_ywae,
                    gold_gem_weight_ywae = gold_gem_weight_ywae,
                    gq_in_carat = gq_in_carat,
                    has_general_expenses = has_general_expenses,
                    imageId = imageId,
                    imageFile = imageFile,
                    impurities_weight_ywae = impurities_weight_ywae,
                    maintenance_cost = maintenance_cost,
                    price_for_pawn = price_for_pawn,
                    pt_and_clip_cost = pt_and_clip_cost,
                    qty = qty,
                    rebuy_price = rebuy_price,
                    size = size,
                    stock_condition = stock_condition,
                    stock_name = stock_name,
                    type = type,
                    wastage_ywae = wastage_ywae,
                    rebuy_price_vertical_option = rebuy_price_vertical_option,
                    productIdList = productIdList,
                    sessionKey = localDatabase.getStockFromHomeSessionKey().orEmpty(),
                    isEditable = isEditable,
                    isChecked = isChecked
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
        return localDatabase.getGoldWeightYwaeForStockFromHome()?:"0"
    }
    fun getCustomerId():String{
        return localDatabase.getAccessCustomerId().orEmpty()
    }
    init {
        getGoldTypePrice()
    }
}