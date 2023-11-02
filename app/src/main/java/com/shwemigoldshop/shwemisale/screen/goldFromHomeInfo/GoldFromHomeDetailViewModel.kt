package com.shwemigoldshop.shwemisale.screen.goldFromHomeInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.shwemigoldshop.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.shwemigoldshop.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.shwemigoldshop.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.shwemigoldshop.shwemisale.room_database.AppDatabase
import com.shwemigoldshop.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GoldFromHomeDetailViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val appDatabase: AppDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase
) : ViewModel() {
    var nameTag = ""
    var rebuyItemList = listOf<RebuyItemDto>()
    var totalQty = 0

    var horizontalOption = "Damage"
    var verticalOption = "X"
    var size = "small"

    var goldPrice = "0"
    var `goldPrice18K` = "0"
    var pawnDiffValue = "0"


    private val _rebuyPriceLiveData = MutableLiveData<Resource<RebuyPriceDto>>()
    val rebuyPriceLiveData: LiveData<Resource<RebuyPriceDto>>
        get() = _rebuyPriceLiveData

    fun getRebuyPrice(horizontal: String, vertical: String, size: String) {
        viewModelScope.launch {
            _rebuyPriceLiveData.value =
                goldFromHomeRepositoryImpl.getRebuyPrice(horizontal, vertical, size)
        }
    }

    private val _rebuyItemeLiveData = MutableLiveData<Resource<List<RebuyItemDto>>>()
    val rebuyItemeLiveData: LiveData<Resource<List<RebuyItemDto>>>
        get() = _rebuyItemeLiveData

    fun getRebuyItem(size: String) {
        _rebuyItemeLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _rebuyItemeLiveData.value = goldFromHomeRepositoryImpl.getRebuyItem(size)
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

    fun getPawnDiffValue() {
        viewModelScope.launch {
            pawnDiffValue = goldFromHomeRepositoryImpl.getPawnDiffValue().data ?: "0"
        }
    }

    private val _createStockFromHomeInfoLiveData =
        MutableLiveData<Resource<String>>()
    val createStockFromHomeInfoLiveData: LiveData<Resource<String>>
        get() = _createStockFromHomeInfoLiveData

    fun createStockFromHome(
        imageFile: RequestBody?,
        imageId:String?,
        a_buying_price: String,
        b_voucher_buying_value: String,
        c_voucher_buying_price: String,
        calculated_buying_value: String,
        calculated_for_pawn: String,
        d_gold_weight_ywae: String,
        e_price_from_new_voucher: String,
        f_voucher_shown_gold_weight_ywae: String,
        gem_value: String,
        gem_weight_ywae: String,
        gold_gem_weight_ywae: String,
        gold_weight_ywae: String,
        gq_in_carat: String,
        has_general_expenses: String,
        impurities_weight_ywae: String,
        maintenance_cost: String,
        price_for_pawn: String,
        pt_and_clip_cost: String,
        qty: String,
        rebuy_price: String,
        size: String,
        stock_condition: String,
        stock_name: String,
        type: String,
        wastage_ywae: String,
        rebuy_price_vertical_option: String,
        productIdList: List<String>?,
        isEditable: Boolean,
        isChecked: Boolean,
        isPawn: Boolean
    ) {
        viewModelScope.launch {
            val sessionKey = if (isPawn) {
                localDatabase.getPawnOldStockSessionKey()
            } else {
                localDatabase.getStockFromHomeSessionKey()
            }
            val a_buying_price =if (a_buying_price.isEmpty()) null else
                MultipartBody.Part.createFormData("a_buying_price", a_buying_price)
            val imageId = imageId?.let { MultipartBody.Part.createFormData("image[id]", it) }
            val b_voucher_buying_value =if (b_voucher_buying_value.isEmpty()) null else
                MultipartBody.Part.createFormData("b_voucher_buying_value", b_voucher_buying_value)
            val c_voucher_buying_price =if (c_voucher_buying_price.isEmpty()) null else
                MultipartBody.Part.createFormData("c_voucher_buying_price", c_voucher_buying_price)
            val calculated_buying_value =if (calculated_buying_value.isEmpty()) null else
                MultipartBody.Part.createFormData(
                    "calculated_buying_value",
                    calculated_buying_value
                )
            val calculated_for_pawn =if (calculated_for_pawn.isEmpty()) null else
                MultipartBody.Part.createFormData("calculated_for_pawn", calculated_for_pawn)
            val d_gold_weight_ywae =if (d_gold_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("d_gold_weight_ywae", d_gold_weight_ywae)
            val e_price_from_new_voucher =if (e_price_from_new_voucher.isEmpty()) null else
                MultipartBody.Part.createFormData(
                    "e_price_from_new_voucher",
                    e_price_from_new_voucher
                )
            val f_voucher_shown_gold_weight_ywae =if (f_voucher_shown_gold_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData(
                    "f_voucher_shown_gold_weight_ywae",
                    f_voucher_shown_gold_weight_ywae
                )
            val gem_value = if (gem_value.isEmpty()) null else
            MultipartBody.Part.createFormData("gem_value", gem_value)

            val gem_weight_ywae =if (gem_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("gem_weight_ywae", gem_weight_ywae)
            val gold_gem_weight_ywae =if (gold_gem_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("gold_gem_weight_ywae", gold_gem_weight_ywae)
            val gold_weight_ywae =if (gold_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("gold_weight_ywae", gold_weight_ywae)
            val gq_in_carat = MultipartBody.Part.createFormData("gq_in_carat", gq_in_carat)
            val has_general_expenses =if (has_general_expenses.isEmpty()) null else
                MultipartBody.Part.createFormData("has_general_expenses", has_general_expenses)
            val image = if (imageFile != null) {
                MultipartBody.Part.createFormData(
                    "image[file]",
                    File(selectedImagePath).name,
                    imageFile
                )
            } else null
            val impurities_weight_ywae =if (impurities_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("impurities_weight_ywae", impurities_weight_ywae)
            val maintenanceCost = if (maintenance_cost.isEmpty()) null else
                MultipartBody.Part.createFormData("maintenance_cost", maintenance_cost)
            val price_for_pawn =if (price_for_pawn.isEmpty()) null else MultipartBody.Part.createFormData("price_for_pawn", price_for_pawn)
            val pt_and_clip_cost =if (pt_and_clip_cost.isEmpty()) null else
                MultipartBody.Part.createFormData("pt_and_clip_cost", pt_and_clip_cost)
            val qty = MultipartBody.Part.createFormData("qty", qty)
            val rebuy_price =if (rebuy_price.isEmpty()) null else MultipartBody.Part.createFormData("rebuy_price", rebuy_price)
            val size =if (size.isEmpty()) null else MultipartBody.Part.createFormData("size", size)
            val stock_condition =if (stock_condition.isEmpty()) null else
                MultipartBody.Part.createFormData("stock_condition", stock_condition)
            val stock_name = if (stock_name.isEmpty()) null else MultipartBody.Part.createFormData("stock_name", stock_name)
            val type =if (type.isEmpty()) null else MultipartBody.Part.createFormData("type", type)
            val wastage_ywae = if (wastage_ywae.isEmpty()) null else MultipartBody.Part.createFormData("wastage_ywae", wastage_ywae)
            val rebuy_price_vertical_option =
                MultipartBody.Part.createFormData(
                    "rebuy_price_vertical_option",
                    rebuy_price_vertical_option
                )
            val productIdListMultipartBody = mutableListOf<MultipartBody.Part>()
            productIdList?.forEach {
                productIdListMultipartBody.add(
                    MultipartBody.Part.createFormData(
                        "products[]",
                        it
                    )
                )
            }
            val isEditableMultiPart =
                MultipartBody.Part.createFormData("is_editable", if (isEditable) "1" else "0")
            val isCheckedMultiPart =
                MultipartBody.Part.createFormData("is_checked", if (isChecked) "1" else "0")

            _createStockFromHomeInfoLiveData.value = Resource.Loading()
            _createStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.createStockFromHomeList(
                    a_buying_price = a_buying_price,
                    b_voucher_buying_value = b_voucher_buying_value,
                    c_voucher_buying_price = c_voucher_buying_price,
                    calculated_buying_value = calculated_buying_value,
                    calculated_for_pawn = calculated_for_pawn,
                    d_gold_weight_ywae = d_gold_weight_ywae,
                    e_price_from_new_voucher = e_price_from_new_voucher,
                    f_voucher_shown_gold_weight_ywae = f_voucher_shown_gold_weight_ywae,
                    gem_value = gem_value,
                    gem_weight_details_session_key = MultipartBody.Part.createFormData(
                        "gem_weight_details_session_key",
                        localDatabase.getGemWeightDetailSessionKey().orEmpty()
                    ),
                    gem_weight_ywae = gem_weight_ywae,
                    gold_weight_ywae = gold_weight_ywae,
                    gold_gem_weight_ywae = gold_gem_weight_ywae,
                    gq_in_carat = gq_in_carat,
                    has_general_expenses = has_general_expenses,
                    imageFile = image,
                    imageId = imageId,
                    impurities_weight_ywae = impurities_weight_ywae,
                    maintenance_cost = maintenanceCost,
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
                    productIdList = productIdListMultipartBody,
                    sessionKey = sessionKey,
                    isPawn = isPawn,
                    isEditable = isEditableMultiPart,
                    isChecked = isCheckedMultiPart
                )
        }
    }

    init {
        getGoldTypePrice()
        getPawnDiffValue()
    }

    private val _updateStockFromHomeInfoLiveData =
        MutableLiveData<Resource<String>>()
    val updateStockFromHomeInfoLiveData: LiveData<Resource<String>>
        get() = _updateStockFromHomeInfoLiveData

    fun updateStockFromHome(
        id: String,
        a_buying_price: String,
        b_voucher_buying_value: String,
        c_voucher_buying_price: String,
        calculated_buying_value: String,
        calculated_for_pawn: String,
        d_gold_weight_ywae: String,
        e_price_from_new_voucher: String,
        f_voucher_shown_gold_weight_ywae: String,
        gem_value: String,
        gem_weight_ywae: String,
        gold_gem_weight_ywae: String,
        gold_weight_ywae: String,
        gq_in_carat: String,
        has_general_expenses: String,
        imageFile: RequestBody?,
        impurities_weight_ywae: String,
        maintenance_cost: String,
        price_for_pawn: String,
        pt_and_clip_cost: String,
        qty: String,
        rebuy_price: String,
        size: String,
        stock_condition: String,
        stock_name: String,
        type: String,
        wastage_ywae: String,
        rebuy_price_vertical_option: String,
        productIdList: List<String>?,
        isEditable: Boolean,
        isChecked: Boolean,
        isPawn: Boolean

    ) {
        viewModelScope.launch {
            val sessionKey = if (isPawn) {
                localDatabase.getPawnOldStockSessionKey()
            } else {
                localDatabase.getStockFromHomeSessionKey()
            }
            val id = MultipartBody.Part.createFormData("id", id)
            val a_buying_price =if (a_buying_price.isEmpty()) null else
                MultipartBody.Part.createFormData("a_buying_price", a_buying_price)
            val b_voucher_buying_value =if (b_voucher_buying_value.isEmpty()) null else
                MultipartBody.Part.createFormData("b_voucher_buying_value", b_voucher_buying_value)
            val c_voucher_buying_price =if (c_voucher_buying_price.isEmpty()) null else
                MultipartBody.Part.createFormData("c_voucher_buying_price", c_voucher_buying_price)
            val calculated_buying_value =if (calculated_buying_value.isEmpty()) null else
                MultipartBody.Part.createFormData(
                    "calculated_buying_value",
                    calculated_buying_value
                )
            val calculated_for_pawn =if (calculated_for_pawn.isEmpty()) null else
                MultipartBody.Part.createFormData("calculated_for_pawn", calculated_for_pawn)
            val d_gold_weight_ywae =if (d_gold_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("d_gold_weight_ywae", d_gold_weight_ywae)
            val e_price_from_new_voucher =if (e_price_from_new_voucher.isEmpty()) null else
                MultipartBody.Part.createFormData(
                    "e_price_from_new_voucher",
                    e_price_from_new_voucher
                )
            val f_voucher_shown_gold_weight_ywae =if (f_voucher_shown_gold_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData(
                    "f_voucher_shown_gold_weight_ywae",
                    f_voucher_shown_gold_weight_ywae
                )
            val gem_value = if (gem_value.isEmpty()) null else
                MultipartBody.Part.createFormData("gem_value", gem_value)

            val gem_weight_ywae =if (gem_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("gem_weight_ywae", gem_weight_ywae)
            val gold_gem_weight_ywae =if (gold_gem_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("gold_gem_weight_ywae", gold_gem_weight_ywae)
            val gold_weight_ywae =if (gold_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("gold_weight_ywae", gold_weight_ywae)
            val gq_in_carat = MultipartBody.Part.createFormData("gq_in_carat", gq_in_carat)
            val has_general_expenses =if (has_general_expenses.isEmpty()) null else
                MultipartBody.Part.createFormData("has_general_expenses", has_general_expenses)
            val image = if (imageFile != null) {
                MultipartBody.Part.createFormData(
                    "image[file]",
                    File(selectedImagePath).name,
                    imageFile
                )
            } else null
            val impurities_weight_ywae =if (impurities_weight_ywae.isEmpty()) null else
                MultipartBody.Part.createFormData("impurities_weight_ywae", impurities_weight_ywae)
            val maintenanceCost = if (maintenance_cost.isEmpty()) null else
                MultipartBody.Part.createFormData("maintenance_cost", maintenance_cost)
            val price_for_pawn =if (price_for_pawn.isEmpty()) null else MultipartBody.Part.createFormData("price_for_pawn", price_for_pawn)
            val pt_and_clip_cost =if (pt_and_clip_cost.isEmpty()) null else
                MultipartBody.Part.createFormData("pt_and_clip_cost", pt_and_clip_cost)
            val qty = MultipartBody.Part.createFormData("qty", qty)
            val rebuy_price =if (rebuy_price.isEmpty()) null else MultipartBody.Part.createFormData("rebuy_price", rebuy_price)
            val size =if (size.isEmpty()) null else MultipartBody.Part.createFormData("size", size)
            val stock_condition =if (stock_condition.isEmpty()) null else
                MultipartBody.Part.createFormData("stock_condition", stock_condition)
            val stock_name = if (stock_name.isEmpty()) null else MultipartBody.Part.createFormData("stock_name", stock_name)
            val type =if (type.isEmpty()) null else MultipartBody.Part.createFormData("type", type)
            val wastage_ywae = if (wastage_ywae.isEmpty()) null else MultipartBody.Part.createFormData("wastage_ywae", wastage_ywae)
            val rebuy_price_vertical_option =
                MultipartBody.Part.createFormData(
                    "rebuy_price_vertical_option",
                    rebuy_price_vertical_option
                )
            val productIdListMultipartBody = mutableListOf<MultipartBody.Part>()
            productIdList?.forEach {
                productIdListMultipartBody.add(
                    MultipartBody.Part.createFormData(
                        "products[]",
                        it
                    )
                )
            }
            val isEditableMultiPart =
                MultipartBody.Part.createFormData("is_editable", if (isEditable) "1" else "0")
            val isCheckedMultiPart =
                MultipartBody.Part.createFormData("is_checked", if (isChecked) "1" else "0")

            _updateStockFromHomeInfoLiveData.value = Resource.Loading()
            _updateStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.updateStockFromHomeList(
                    id = id,
                    a_buying_price = a_buying_price,
                    b_voucher_buying_value = b_voucher_buying_value,
                    c_voucher_buying_price = c_voucher_buying_price,
                    calculated_buying_value = calculated_buying_value,
                    calculated_for_pawn = calculated_for_pawn,
                    d_gold_weight_ywae = d_gold_weight_ywae,
                    e_price_from_new_voucher = e_price_from_new_voucher,
                    f_voucher_shown_gold_weight_ywae = f_voucher_shown_gold_weight_ywae,
                    gem_value = gem_value,
                    gem_weight_details_session_key = MultipartBody.Part.createFormData(
                        "gem_weight_details_session_key",
                        localDatabase.getGemWeightDetailSessionKey().orEmpty()
                    ),
                    gem_weight_ywae = gem_weight_ywae,
                    gold_weight_ywae = gold_weight_ywae,
                    gold_gem_weight_ywae = gold_gem_weight_ywae,
                    gq_in_carat = gq_in_carat,
                    has_general_expenses = has_general_expenses,
                    imageFile = image,
                    impurities_weight_ywae = impurities_weight_ywae,
                    maintenance_cost = maintenanceCost,
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
                    productIdList = productIdListMultipartBody,
                    sessionKey = sessionKey,
                    isEditable = isEditableMultiPart,
                    isChecked = isCheckedMultiPart,
                    isPawn = isPawn
                )
        }
    }

    private val _getGemWeightDetailLiveData =
        MutableLiveData<Resource<List<GemWeightDetailDomain>>>()
    val getGemWeightDetailLiveData: LiveData<Resource<List<GemWeightDetailDomain>>>
        get() = _getGemWeightDetailLiveData

    fun getGemWeightDetaiil() {
        viewModelScope.launch {
            _getGemWeightDetailLiveData.value = Resource.Loading()
            _getGemWeightDetailLiveData.value = goldFromHomeRepositoryImpl.getGemWeightDetail(
                localDatabase.getGemWeightDetailSessionKey().orEmpty()
            )
        }
    }

    private val _createGemWeightDetailLiveData =
        SingleLiveEvent<Resource<String>>()
    val createGemWeightDetailLiveData: SingleLiveEvent<Resource<String>>
        get() = _createGemWeightDetailLiveData

    fun createGemWeightDetaiil(
        qty: String,
        weightGmPerUnit: String,
        weightYwaePerUnit: String
    ) {
        viewModelScope.launch {
            _createGemWeightDetailLiveData.value = Resource.Loading()
            _createGemWeightDetailLiveData.value = goldFromHomeRepositoryImpl.createGemWeightDetail(
                qty.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                weightGmPerUnit.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                weightYwaePerUnit.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }
    }

    private val _updateGemWeightDetailLiveData =
        SingleLiveEvent<Resource<String>>()
    val updateGemWeightDetailLiveData: SingleLiveEvent<Resource<String>>
        get() = _updateGemWeightDetailLiveData
    fun updateGemWeightDetaiil(
        id: String,
        qty: String,
        weightGmPerUnit: String,
        weightYwaePerUnit: String
    ) {
        viewModelScope.launch {
            _updateGemWeightDetailLiveData.value = Resource.Loading()
            _updateGemWeightDetailLiveData.value = goldFromHomeRepositoryImpl.updateGemWeightDetail(
                id.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                qty.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                weightGmPerUnit.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                weightYwaePerUnit.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }
    }

    private val _deleteGemWeightDetailLiveData =
        SingleLiveEvent<Resource<String>>()
    val deleteGemWeightDetailLiveData: SingleLiveEvent<Resource<String>>
        get() = _deleteGemWeightDetailLiveData
    fun deleteGemWeightDetaiil(
        id: String
    ) {
        viewModelScope.launch {
            _deleteGemWeightDetailLiveData.value = Resource.Loading()
            _deleteGemWeightDetailLiveData.value = goldFromHomeRepositoryImpl.deleteGemWeightDetail(
                id
            )
        }
    }


    var selectedImagePath: String? = null

}