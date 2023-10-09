package com.example.shwemisale.screen.oldStockDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemWithSize
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class OldStockDetailViewModel @Inject constructor(
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

    var goldAndGemWeightGm = 0.0
    var goldAndGemWeightYwae = 0.0
    var gemWeightYwae = 0.0

    fun getChoosenStockTypeAndTotalQty(size: String) {
        val currentState = _rebuyItemeLiveData.value as? Resource.Success<RebuyItemWithSize>

        if (size == "small") {

            rebuyItemList = currentState?.data?.smallSizeItems?.filter { it.qty > 0 }.orEmpty()
            var totalqty = 0
            var name = ""
            rebuyItemList.forEach {
                totalqty += it.qty
                name += it.name + ":" + it.qty.toString() + ","
            }
            nameTag = name.dropLast(1)
            totalQty = totalqty
        } else {
            rebuyItemList = currentState?.data?.largeSizeItems?.filter { it.qty > 0 }.orEmpty()

            var totalqty = 0
            var name = ""
            rebuyItemList.forEach {
                totalqty += it.qty
                name += it.name + ":" + it.qty.toString() + ","
            }
            nameTag = name.dropLast(1)
            val resultName = name.dropLast(1)
            totalQty = totalqty
        }

    }

    fun qtyIncrease(id: String, size: String) {
        val currentState = _rebuyItemeLiveData.value as? Resource.Success<RebuyItemWithSize>
        _rebuyItemeLiveData.value = Resource.Success(
            if (size == "small") {
                currentState?.data?.copy(
                    smallSizeItems = increaseQty(currentState.data.smallSizeItems, id)
                )
            } else {
                currentState?.data?.copy(
                    largeSizeItems = increaseQty(currentState.data.largeSizeItems, id)
                )
            }
        )
    }

    private fun increaseQty(list: List<RebuyItemDto>?, id: String): List<RebuyItemDto> {
        return list?.map {
            if (it.id == id) {
                it.copy(qty = it.qty + 1)
            } else {
                it
            }
        }.orEmpty()
    }

    private fun decreaseQty(list: List<RebuyItemDto>?, id: String): List<RebuyItemDto> {
        return list?.map {
            if (it.id == id) {
                it.copy(qty = it.qty - 1)
            } else {
                it
            }
        }.orEmpty()
    }

    private fun changeName(
        list: List<RebuyItemDto>?,
        id: String,
        text: String
    ): List<RebuyItemDto> {
        return list?.map {
            if (it.id == id) {
                it.copy(name = text, hasNameModified = true)
            } else {
                it
            }
        }.orEmpty()
    }

    fun qtyDecrease(id: String, size: String) {
        val currentState = _rebuyItemeLiveData.value as? Resource.Success<RebuyItemWithSize>
        _rebuyItemeLiveData.value = Resource.Success(
            if (size == "small") {
                currentState?.data?.copy(
                    smallSizeItems = decreaseQty(currentState.data.smallSizeItems, id)
                )
            } else {
                currentState?.data?.copy(
                    largeSizeItems = decreaseQty(currentState.data.largeSizeItems, id)
                )
            }
        )
    }


    fun onNameChanged(id: String, text: String, size: String) {
        val currentState = _rebuyItemeLiveData.value as? Resource.Success<RebuyItemWithSize>
        val isTheSameName = if (size == "small") {
            currentState?.data?.smallSizeItems
        } else {
            currentState?.data?.largeSizeItems
        }?.find { it.id == id }?.name == text

        if (!isTheSameName) {
            _rebuyItemeLiveData.value = Resource.Success(
                if (size == "small") {
                    currentState?.data?.copy(
                        smallSizeItems = changeName(currentState.data.smallSizeItems, id, text)
                    )
                } else {
                    currentState?.data?.copy(
                        largeSizeItems = changeName(currentState.data.largeSizeItems, id, text)
                    )
                }
            )
        }
    }

    fun changeToEditView(id: String, size: String, isEditable: Boolean) {
        val currentState = _rebuyItemeLiveData.value as? Resource.Success<RebuyItemWithSize>
        _rebuyItemeLiveData.value = Resource.Success(
            if (size == "small") {
                currentState?.data?.copy(
                    smallSizeItems = changeEditView(
                        currentState.data.smallSizeItems,
                        id,
                        isEditable
                    )
                )
            } else {
                currentState?.data?.copy(
                    largeSizeItems = changeEditView(
                        currentState.data.largeSizeItems,
                        id,
                        isEditable
                    )
                )
            }
        )
    }

    private fun changeEditView(
        list: List<RebuyItemDto>?,
        id: String,
        isEditing: Boolean
    ): List<RebuyItemDto> {
        return list?.map {
            if (it.id == id) {
                it.copy(isEditing = isEditing, canEdit = true)
            } else {
                it.copy(canEdit = !isEditing)
            }
        }.orEmpty()
    }


    private val _rebuyPriceLiveData = MutableLiveData<Resource<RebuyPriceDto>>()
    val rebuyPriceLiveData: LiveData<Resource<RebuyPriceDto>>
        get() = _rebuyPriceLiveData

    fun getRebuyPrice(horizontal: String, vertical: String, size: String) {
        viewModelScope.launch {
            _rebuyPriceLiveData.value =
                goldFromHomeRepositoryImpl.getRebuyPrice(horizontal, vertical, size)
        }
    }

    private val _rebuyItemeLiveData = MutableLiveData<Resource<RebuyItemWithSize>>()
    val rebuyItemeLiveData: LiveData<Resource<RebuyItemWithSize>>
        get() = _rebuyItemeLiveData.distinctUntilChanged()

    fun getRebuyItem(size: String) {
        viewModelScope.launch {
            val result = goldFromHomeRepositoryImpl.getRebuyItem(size)
            val currentState = _rebuyItemeLiveData.value as? Resource.Success<RebuyItemWithSize>
            _rebuyItemeLiveData.value = when (result) {
                is Resource.Success -> {
                    if (size == "small") {
                        val currentSmallItems = currentState?.data?.smallSizeItems
                        RebuyItemWithSize(
                            smallSizeItems = getUpdatedData(currentSmallItems, result.data),
                            largeSizeItems = currentState?.data?.largeSizeItems ?: emptyList()
                        )
                    } else {
                        val currentLargeItems = currentState?.data?.largeSizeItems
                        RebuyItemWithSize(
                            largeSizeItems = getUpdatedData(currentLargeItems, result.data),
                            smallSizeItems = currentState?.data?.smallSizeItems ?: emptyList()
                        )
                    }.let { Resource.Success(it) }
                }

                is Resource.Error -> Resource.Error(result.message)
                is Resource.Loading -> Resource.Loading()

            }

        }
    }

    fun getUpdatedData(
        currentItems: List<RebuyItemDto>?,
        newItems: List<RebuyItemDto>?
    ): List<RebuyItemDto> {
        return newItems?.map { newItem ->
            val oldItem = currentItems?.find { it.id == newItem.id }
            oldItem?.copy(
                name = if (oldItem.hasNameModified) {
                    oldItem.name
                } else {
                    newItem.name
                },
                canEdit = true
            ) ?: newItem.copy(canEdit = true)

        }.orEmpty()
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
        imageId: String?,
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
            val a_buying_price = MultipartBody.Part.createFormData("a_buying_price", a_buying_price)
            val imageId = imageId?.let { MultipartBody.Part.createFormData("image[id]", it) }
            val b_voucher_buying_value =
                MultipartBody.Part.createFormData("b_voucher_buying_value", b_voucher_buying_value)
            val c_voucher_buying_price =
                MultipartBody.Part.createFormData("c_voucher_buying_price", c_voucher_buying_price)
            val calculated_buying_value =
                MultipartBody.Part.createFormData(
                    "calculated_buying_value",
                    calculated_buying_value
                )
            val calculated_for_pawn =
                MultipartBody.Part.createFormData("calculated_for_pawn", calculated_for_pawn)
            val d_gold_weight_ywae =
                MultipartBody.Part.createFormData("d_gold_weight_ywae", d_gold_weight_ywae)
            val e_price_from_new_voucher =
                MultipartBody.Part.createFormData(
                    "e_price_from_new_voucher",
                    e_price_from_new_voucher
                )
            val f_voucher_shown_gold_weight_ywae =
                MultipartBody.Part.createFormData(
                    "f_voucher_shown_gold_weight_ywae",
                    f_voucher_shown_gold_weight_ywae
                )
            val gem_value = MultipartBody.Part.createFormData("gem_value", gem_value)

            val gem_weight_ywae =
                MultipartBody.Part.createFormData("gem_weight_ywae", gem_weight_ywae)
            val gold_gem_weight_ywae =
                MultipartBody.Part.createFormData("gold_gem_weight_ywae", gold_gem_weight_ywae)
            val gold_weight_ywae =
                MultipartBody.Part.createFormData("gold_weight_ywae", gold_weight_ywae)
            val gq_in_carat = MultipartBody.Part.createFormData("gq_in_carat", gq_in_carat)
            val has_general_expenses =
                MultipartBody.Part.createFormData("has_general_expenses", has_general_expenses)
            val image = if (imageFile != null) {
                MultipartBody.Part.createFormData(
                    "image[file]",
                    File(selectedImagePath).name,
                    imageFile
                )
            } else null
            val impurities_weight_ywae =
                MultipartBody.Part.createFormData("impurities_weight_ywae", impurities_weight_ywae)
            val maintenance_cost =
                MultipartBody.Part.createFormData("maintenance_cost", maintenance_cost)
            val price_for_pawn = MultipartBody.Part.createFormData("price_for_pawn", price_for_pawn)
            val pt_and_clip_cost =
                MultipartBody.Part.createFormData("pt_and_clip_cost", pt_and_clip_cost)
            val qty = MultipartBody.Part.createFormData("qty", qty)
            val rebuy_price = MultipartBody.Part.createFormData("rebuy_price", rebuy_price)
            val size = MultipartBody.Part.createFormData("size", size)
            val stock_condition =
                MultipartBody.Part.createFormData("stock_condition", stock_condition)
            val stock_name = MultipartBody.Part.createFormData("stock_name", stock_name)
            val type = MultipartBody.Part.createFormData("type", type)
            val wastage_ywae = MultipartBody.Part.createFormData("wastage_ywae", wastage_ywae)
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
            val a_buying_price = MultipartBody.Part.createFormData("a_buying_price", a_buying_price)
            val b_voucher_buying_value =
                MultipartBody.Part.createFormData("b_voucher_buying_value", b_voucher_buying_value)
            val c_voucher_buying_price =
                MultipartBody.Part.createFormData("c_voucher_buying_price", c_voucher_buying_price)
            val calculated_buying_value =
                MultipartBody.Part.createFormData(
                    "calculated_buying_value",
                    calculated_buying_value
                )
            val calculated_for_pawn =
                MultipartBody.Part.createFormData("calculated_for_pawn", calculated_for_pawn)
            val d_gold_weight_ywae =
                MultipartBody.Part.createFormData("d_gold_weight_ywae", d_gold_weight_ywae)
            val e_price_from_new_voucher =
                MultipartBody.Part.createFormData(
                    "e_price_from_new_voucher",
                    e_price_from_new_voucher
                )
            val f_voucher_shown_gold_weight_ywae =
                MultipartBody.Part.createFormData(
                    "f_voucher_shown_gold_weight_ywae",
                    f_voucher_shown_gold_weight_ywae
                )
            val gem_value = MultipartBody.Part.createFormData("gem_value", gem_value)

            val gem_weight_ywae =
                MultipartBody.Part.createFormData("gem_weight_ywae", gem_weight_ywae)
            val gold_gem_weight_ywae =
                MultipartBody.Part.createFormData("gold_gem_weight_ywae", gold_gem_weight_ywae)
            val gold_weight_ywae =
                MultipartBody.Part.createFormData("gold_weight_ywae", gold_weight_ywae)
            val gq_in_carat = MultipartBody.Part.createFormData("gq_in_carat", gq_in_carat)
            val has_general_expenses =
                MultipartBody.Part.createFormData("has_general_expenses", has_general_expenses)
            val image = if (imageFile != null) {
                MultipartBody.Part.createFormData(
                    "image[file]",
                    File(selectedImagePath).name,
                    imageFile
                )
            } else null
            val impurities_weight_ywae =
                MultipartBody.Part.createFormData("impurities_weight_ywae", impurities_weight_ywae)
            val maintenance_cost =
                MultipartBody.Part.createFormData("maintenance_cost", maintenance_cost)
            val price_for_pawn = MultipartBody.Part.createFormData("price_for_pawn", price_for_pawn)
            val pt_and_clip_cost =
                MultipartBody.Part.createFormData("pt_and_clip_cost", pt_and_clip_cost)
            val qty = MultipartBody.Part.createFormData("qty", qty)
            val rebuy_price = MultipartBody.Part.createFormData("rebuy_price", rebuy_price)
            val size = MultipartBody.Part.createFormData("size", size)
            val stock_condition =
                MultipartBody.Part.createFormData("stock_condition", stock_condition)
            val stock_name = MultipartBody.Part.createFormData("stock_name", stock_name)
            val type = MultipartBody.Part.createFormData("type", type)
            val wastage_ywae = MultipartBody.Part.createFormData("wastage_ywae", wastage_ywae)
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

    fun onChangeGemQty(id: String, qty: Int) {
        val currentState =
            _getGemWeightDetailLiveData.value as? Resource.Success<List<GemWeightDetailDomain>>
        val isTheSameQty = currentState?.data?.find { it.id == id }?.gem_qty == qty
        if (!isTheSameQty){
            _getGemWeightDetailLiveData.value = Resource.Success(changeGemQty(id,qty,currentState?.data.orEmpty()))
        }
    }

    fun changeGemQty(id:String,qty: Int,list:List<GemWeightDetailDomain>):List<GemWeightDetailDomain>{
        return list.map {
            if (it.id == id) {
                it.copy(gem_qty = qty)
            } else {
                it
            }
        }
    }
    fun onChangeGemWeightGm(id: String, weightGm: Double) {
        val currentState =
            _getGemWeightDetailLiveData.value as? Resource.Success<List<GemWeightDetailDomain>>
        val isTheSameWeightGm = currentState?.data?.find { it.id == id }?.gem_weight_gm_per_unit == weightGm
        if (!isTheSameWeightGm){
            _getGemWeightDetailLiveData.value = Resource.Success(changeGemWeightGm(id,weightGm,currentState?.data.orEmpty()))
        }
    }

    fun changeGemWeightGm(id:String,weightGm: Double,list:List<GemWeightDetailDomain>):List<GemWeightDetailDomain>{
        return list.map {
            if (it.id == id) {
                it.copy(gem_weight_gm_per_unit = weightGm)
            } else {
                it
            }
        }
    }
    fun onChangeTotalGemWeight(id: String,qty:Int, gemWeightYwaePerUnit: Double) {
        val currentState =
            _getGemWeightDetailLiveData.value as? Resource.Success<List<GemWeightDetailDomain>>
        val totalGemWeight = qty*gemWeightYwaePerUnit
        val isTheSameTotalGemWeight = currentState?.data?.find { it.id == id }?.totalWeightYwae == totalGemWeight
        if (!isTheSameTotalGemWeight){
            _getGemWeightDetailLiveData.value = Resource.Success(changeTotalGemWeight(id,totalGemWeight,currentState?.data.orEmpty()))
        }
    }

    fun getTotalCalculatedGemWeightYwae():Double{
        val currentState =
            _getGemWeightDetailLiveData.value as? Resource.Success<List<GemWeightDetailDomain>>
        var totalWeight = 0.0
        currentState?.data?.forEach {
            totalWeight += it.totalWeightYwae
        }
        return totalWeight
    }
    fun changeTotalGemWeight(id:String,totalGemWeight: Double,list:List<GemWeightDetailDomain>):List<GemWeightDetailDomain>{
        return list.map {
            if (it.id == id) {
                it.copy(totalWeightYwae = totalGemWeight)
            } else {
                it
            }
        }
    }

    fun onChangeGemWeightDetailKpy(id: String, ywae: Double) {
        val currentState =
            _getGemWeightDetailLiveData.value as? Resource.Success<List<GemWeightDetailDomain>>
        val isTheSameKpy =
            currentState?.data?.find { it.id == id }?.gem_weight_ywae_per_unit == ywae
        if (!isTheSameKpy){
            _getGemWeightDetailLiveData.value = Resource.Success(changeGemWeightDetailKpy(id,ywae,currentState?.data.orEmpty()))
        }
    }

    fun changeGemWeightDetailKpy(id: String, ywae: Double,list:List<GemWeightDetailDomain>):List<GemWeightDetailDomain>{
        return list.map {
            if (it.id == id) {
                it.copy(gem_weight_ywae_per_unit = ywae)
            } else {
                it
            }
        }
    }

    fun getGemWeightDetaiil() {
        viewModelScope.launch {
//            _getGemWeightDetailLiveData.value = Resource.Success(
//                listOf(
//                    GemWeightDetailDomain(
//                        "1",
//                        0,
//                        0.0,
//                        0.0,
//                        0.0,
//                        null
//                    ),
//                    GemWeightDetailDomain(
//                        "2",
//                        0,
//                        0.0,
//                        0.0,
//                        0.0,
//                        null
//                    ),
//                    GemWeightDetailDomain(
//                        "3",
//                        0,
//                        0.0,
//                        0.0,
//                        0.0,
//                        null
//                    )
//                )
//            )
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