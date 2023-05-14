package com.example.shwemisale.screen.goldFromHomeInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemi.util.compressImage
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.StockFromHomeInfoEntity
import com.example.shwemisale.room_database.entity.asUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
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

    var gemWeightCustomList = mutableListOf<GemWeightDetailDomain>()
    var gemWeightCustomListLiveData = MutableLiveData<List<GemWeightDetailDomain>>()
    fun addGemDetailList(item: List<GemWeightDetailDomain>) {
        gemWeightCustomList.addAll(item)
        gemWeightCustomListLiveData.value = gemWeightCustomList
    }

    fun addGemDetail(item: GemWeightDetailDomain) {
        gemWeightCustomList.add(item)
        gemWeightCustomListLiveData.value = gemWeightCustomList
    }

    fun removeGemDetail(item: GemWeightDetailDomain) {
        gemWeightCustomList.remove(item)
        gemWeightCustomListLiveData.value = gemWeightCustomList
    }

//    fun addGemWeightCustom(item:GemWeightInStockFromHome){
//        gemWeightCustomList.add(item)
//        gemWeightCustom.value = gemWeightCustomList
//    }
//    fun removeGemWeightCustom(id: String){
//        gemWeightCustomList.remove(gemWeightCustomList.find { it.id == id })
//        gemWeightCustom.value = gemWeightCustomList
//    }
//    fun resetGemWeightCustom(){
//        gemWeightCustomList = mutableListOf()
//        gemWeightCustom.value = gemWeightCustomList
//    }


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
        itemList: List<StockFromHomeDomain>,
        isPawn: Boolean,
    ) {
        viewModelScope.launch {
            val updatedList = itemList
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
            repeat(updatedList.size) {
                val gemQtyList = updatedList[it].gem_weight_details.orEmpty().map { it.gem_qty }
                val gemWeightYwaeList =
                    updatedList[it].gem_weight_details.orEmpty().map { it.gem_weight_ywae_per_unit }
                val gemWeightGmList =
                    updatedList[it].gem_weight_details.orEmpty().map { it.gem_weight_gm_per_unit }

                repeat(updatedList[it].gem_weight_details.orEmpty().size) { gemWeightIndex ->
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
                        updatedList[it].a_buying_price.toString()
                    )
                )

                b_voucher_buying_value.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][b_voucher_buying_value]",
                        updatedList[it].b_voucher_buying_value.toString()
                    )
                )

                c_voucher_buying_price.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][c_voucher_buying_price]",
                        updatedList[it].c_voucher_buying_price.toString()
                    )
                )

                calculated_buying_value.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][calculated_buying_value]",
                        updatedList[it].calculated_buying_value.toString()
                    )
                )

                calculated_for_pawn.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][calculated_for_pawn]",
                        updatedList[it].calculated_for_pawn.toString()
                    )
                )

                d_gold_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][d_gold_weight_ywae]",
                        updatedList[it].d_gold_weight_ywae.toString()
                    )
                )

                e_price_from_new_voucher.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][e_price_from_new_voucher]",
                        updatedList[it].e_price_from_new_voucher.toString()
                    )
                )

                f_voucher_shown_gold_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][f_voucher_shown_gold_weight_ywae]",
                        updatedList[it].f_voucher_shown_gold_weight_ywae.toString()
                    )
                )

                gem_value.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gem_value]",
                        updatedList[it].gem_value.toString()
                    )
                )

                gem_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gem_weight_ywae]",
                        updatedList[it].gem_weight_ywae.toString()
                    )
                )

                gold_gem_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gold_gem_weight_ywae]",
                        updatedList[it].gold_gem_weight_ywae.toString()
                    )
                )
                gold_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gold_weight_ywae]",
                        updatedList[it].gold_weight_ywae.toString()
                    )
                )
                gq_in_carat.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gq_in_carat]",
                        updatedList[it].gq_in_carat.toString()
                    )
                )
                has_general_expenses.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][has_general_expenses]",
                        updatedList[it].has_general_expenses.toString()
                    )
                )
                updatedList[it].image?.id?.let { id ->
                    imageId.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][image][id]",
                            id
                        )
                    )
                }
                updatedList[it].image?.url?.let { path ->
                    val requestBody = compressImage(path)

                    imageFile.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][image][file]",
                            File(path).name,
                            requestBody)
                        )
                }
//

                impurities_weight_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][impurities_weight_ywae]",
                        updatedList[it].impurities_weight_ywae.toString()
                    )
                )
                maintenance_cost.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][maintenance_cost]",
                        updatedList[it].maintenance_cost.toString()
                    )
                )
                price_for_pawn.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][price_for_pawn]",
                        updatedList[it].price_for_pawn.toString()
                    )
                )
                pt_and_clip_cost.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][pt_and_clip_cost]",
                        updatedList[it].pt_and_clip_cost.toString()
                    )
                )
                qty.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][qty]",
                        updatedList[it].qty.toString()
                    )
                )

                rebuy_price.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][rebuy_price]",
                        updatedList[it].rebuy_price
                    )
                )
                size.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][size]",
                        updatedList[it].size.toString()
                    )
                )
                stock_condition.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][stock_condition]",
                        updatedList[it].stock_condition.toString()
                    )
                )
                stock_name.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][stock_name]",
                        updatedList[it].stock_name.toString()
                    )
                )
                type.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][type]",
                        updatedList[it].type.toString()
                    )
                )
                wastage_ywae.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][wastage_ywae]",
                        updatedList[it].wastage_ywae.toString()
                    )
                )
                rebuy_price_vertical_option.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][rebuy_price_vertical_option]",
                        updatedList[it].rebuy_price_vertical_option.orEmpty()
                    )
                )
                repeat(updatedList[it].productId.orEmpty().size) { index ->
                    productIdList.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][products][]",
                            updatedList[it].productId.orEmpty()[index]
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
            val sessionKey = if (isPawn) {
                localDatabase.getPawnOldStockSessionKey()
            } else {
                localDatabase.getStockFromHomeSessionKey()
            }
            _createStockFromHomeInfoLiveData.value = Resource.Loading()
            _createStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.createStockFromHomeList(
                    id = null,
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
                    sessionKey = sessionKey,
                    isPawn = isPawn,
                    isEditable = isEditable,
                    isChecked = isChecked
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
        a_buying_price_update: String?,
        b_voucher_buying_value_update: String?,
        c_voucher_buying_price_update: String?,
        calculated_buying_value_update: String?,
        calculated_for_pawn_update: String?,
        d_gold_weight_ywae_update: String?,
        e_price_from_new_voucher_update: String?,
        f_voucher_shown_gold_weight_ywae_update: String?,
        gem_value_update: String?,
        gem_weight_ywae_update: String?,
        gold_gem_weight_ywae_update: String?,
        gold_weight_ywae_update: String?,
        gq_in_carat_update: String?,
        has_general_expenses_update: String?,
        imageId_update: String?,
        imageFile_update: RequestBody?,
        impurities_weight_ywae_update: String?,
        maintenance_cost_update: String?,
        price_for_pawn_update: String?,
        pt_and_clip_cost_update: String?,
        qty_update: String?,
        rebuy_price_update: String?,
        size_update: String?,
        stock_condition_update: String?,
        stock_name_update: String?,
        type_update: String?,
        wastage_ywae_update: String?,
        rebuy_price_vertical_option_update: String?,
        item: StockFromHomeDomain,
        itemList: List<StockFromHomeDomain>
    ) {
        viewModelScope.launch {
            val updatedList = itemList
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
            if (updatedList.isNotEmpty()) {
                repeat(updatedList.size) {
                    if (updatedList[it].id == item.id) {
                        val gemQtyList = gemWeightCustomList.map { it.gem_qty }
                        val gemWeightYwaeList =
                            gemWeightCustomList.map { it.gem_weight_ywae_per_unit }
                        val gemWeightGmList =
                            gemWeightCustomList.map { it.gem_weight_gm_per_unit }

                        repeat(gemWeightCustomList.size) { gemWeightIndex ->
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
                                a_buying_price_update.orEmpty()
                            )
                        )

                        b_voucher_buying_value.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][b_voucher_buying_value]",
                                b_voucher_buying_value_update.orEmpty()
                            )
                        )

                        c_voucher_buying_price.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][c_voucher_buying_price]",
                                c_voucher_buying_price_update.orEmpty()
                            )
                        )

                        calculated_buying_value.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][calculated_buying_value]",
                                calculated_buying_value_update.orEmpty()
                            )
                        )

                        calculated_for_pawn.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][calculated_for_pawn]",
                                calculated_for_pawn_update.orEmpty()
                            )
                        )

                        d_gold_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][d_gold_weight_ywae]",
                                d_gold_weight_ywae_update.orEmpty()
                            )
                        )

                        e_price_from_new_voucher.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][e_price_from_new_voucher]",
                                e_price_from_new_voucher_update.orEmpty()
                            )
                        )

                        f_voucher_shown_gold_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][f_voucher_shown_gold_weight_ywae]",
                                f_voucher_shown_gold_weight_ywae_update.orEmpty()
                            )
                        )

                        gem_value.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gem_value]",
                                gem_value_update.orEmpty()
                            )
                        )

                        gem_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gem_weight_ywae]",
                                gem_weight_ywae_update.orEmpty()
                            )
                        )

                        gold_gem_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gold_gem_weight_ywae]",
                                gold_gem_weight_ywae_update.orEmpty()
                            )
                        )
                        gold_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gold_weight_ywae]",
                                gold_weight_ywae_update.orEmpty()
                            )
                        )
                        gq_in_carat.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gq_in_carat]",
                                gq_in_carat_update.orEmpty()
                            )
                        )
                        has_general_expenses.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][has_general_expenses]",
                                has_general_expenses_update.orEmpty()
                            )
                        )

                        if (imageFile_update != null) {
                            imageFile_update.let { requestBody ->
                                imageFile.add(
                                    MultipartBody.Part.createFormData(
                                        "old_stocks[$it][image][file]",
                                        File(selectedImagePath.orEmpty()).name,
                                        requestBody
                                    )
                                )
                            }
                        } else {
                            imageId_update?.let { id ->
                                imageId.add(
                                    MultipartBody.Part.createFormData(
                                        "old_stocks[$it][image][id]",
                                        id
                                    )
                                )
                            }
                        }



                        impurities_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][impurities_weight_ywae]",
                                impurities_weight_ywae_update.orEmpty()
                            )
                        )
                        maintenance_cost.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][maintenance_cost]",
                                maintenance_cost_update.orEmpty()
                            )
                        )
                        price_for_pawn.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][price_for_pawn]",
                                price_for_pawn_update.orEmpty()
                            )
                        )
                        pt_and_clip_cost.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][pt_and_clip_cost]",
                                pt_and_clip_cost_update.orEmpty()
                            )
                        )
                        qty.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][qty]",
                                qty_update.orEmpty()
                            )
                        )
                        rebuy_price.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][rebuy_price]",
                                rebuy_price_update.orEmpty()
                            )
                        )
                        size.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][size]",
                                size_update.orEmpty()
                            )
                        )
                        stock_condition.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][stock_condition]",
                                stock_condition_update.orEmpty()
                            )
                        )
                        stock_name.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][stock_name]",
                                stock_name_update.orEmpty()
                            )
                        )
                        type.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][type]",
                                type_update.orEmpty()
                            )
                        )
                        wastage_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][wastage_ywae]",
                                wastage_ywae_update.orEmpty()
                            )
                        )
                        rebuy_price_vertical_option.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][rebuy_price_vertical_option]",
                                rebuy_price_vertical_option_update.orEmpty()
                            )
                        )
                        repeat(updatedList[it].productId.orEmpty().size) { index ->
                            productIdList.add(
                                MultipartBody.Part.createFormData(
                                    "old_stocks[$it][products][]",
                                    updatedList[it].productId.orEmpty()[index]
                                )
                            )
                        }
                        isEditable.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][is_editable]",
                                if (updatedList[it].isEditable) "1" else "0"
                            )
                        )
                        isChecked.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][is_checked]",
                                if (updatedList[it].isChecked) "1" else "0"
                            )
                        )
                    } else {
                        val gemQtyList =
                            updatedList[it].gem_weight_details.orEmpty().map { it.gem_qty }
                        val gemWeightYwaeList =
                            updatedList[it].gem_weight_details.orEmpty()
                                .map { it.gem_weight_ywae_per_unit }
                        val gemWeightGmList =
                            updatedList[it].gem_weight_details.orEmpty()
                                .map { it.gem_weight_gm_per_unit }

                        repeat(updatedList[it].gem_weight_details.orEmpty().size) { gemWeightIndex ->
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
                                updatedList[it].a_buying_price.toString()
                            )
                        )

                        b_voucher_buying_value.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][b_voucher_buying_value]",
                                updatedList[it].b_voucher_buying_value.toString()
                            )
                        )

                        c_voucher_buying_price.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][c_voucher_buying_price]",
                                updatedList[it].c_voucher_buying_price.toString()
                            )
                        )

                        calculated_buying_value.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][calculated_buying_value]",
                                updatedList[it].calculated_buying_value.toString()
                            )
                        )

                        calculated_for_pawn.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][calculated_for_pawn]",
                                updatedList[it].calculated_for_pawn.toString()
                            )
                        )

                        d_gold_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][d_gold_weight_ywae]",
                                updatedList[it].d_gold_weight_ywae.toString()
                            )
                        )

                        e_price_from_new_voucher.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][e_price_from_new_voucher]",
                                updatedList[it].e_price_from_new_voucher.toString()
                            )
                        )

                        f_voucher_shown_gold_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][f_voucher_shown_gold_weight_ywae]",
                                updatedList[it].f_voucher_shown_gold_weight_ywae.toString()
                            )
                        )

                        gem_value.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gem_value]",
                                updatedList[it].gem_value.toString()
                            )
                        )

                        gem_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gem_weight_ywae]",
                                updatedList[it].gem_weight_ywae.toString()
                            )
                        )

                        gold_gem_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gold_gem_weight_ywae]",
                                updatedList[it].gold_gem_weight_ywae.toString()
                            )
                        )
                        gold_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gold_weight_ywae]",
                                updatedList[it].gold_weight_ywae.toString()
                            )
                        )
                        gq_in_carat.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][gq_in_carat]",
                                updatedList[it].gq_in_carat.toString()
                            )
                        )
                        has_general_expenses.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][has_general_expenses]",
                                updatedList[it].has_general_expenses.toString()
                            )
                        )
                        updatedList[it].image?.id?.let { id ->
                            imageId.add(
                                MultipartBody.Part.createFormData(
                                    "old_stocks[$it][image][id]",
                                    id
                                )
                            )
                        }
//                        updatedList[it].image?.url?.let {url->
//                            imageFile.add(
//                                MultipartBody.Part.createFormData(
//                                    "old_stocks[$it][imageId]",
//                                    url
//                                )
//                            )
//                        }

                        impurities_weight_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][impurities_weight_ywae]",
                                updatedList[it].impurities_weight_ywae.toString()
                            )
                        )
                        maintenance_cost.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][maintenance_cost]",
                                updatedList[it].maintenance_cost.toString()
                            )
                        )
                        price_for_pawn.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][price_for_pawn]",
                                updatedList[it].price_for_pawn.toString()
                            )
                        )
                        pt_and_clip_cost.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][pt_and_clip_cost]",
                                updatedList[it].pt_and_clip_cost.toString()
                            )
                        )
                        qty.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][qty]",
                                updatedList[it].qty.toString()
                            )
                        )
                        rebuy_price.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][rebuy_price]",
                                updatedList[it].rebuy_price.toString()
                            )
                        )
                        size.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][size]",
                                updatedList[it].size.toString()
                            )
                        )
                        stock_condition.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][stock_condition]",
                                updatedList[it].stock_condition.toString()
                            )
                        )
                        stock_name.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][stock_name]",
                                updatedList[it].stock_name.toString()
                            )
                        )
                        type.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][type]",
                                updatedList[it].type.toString()
                            )
                        )
                        wastage_ywae.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][wastage_ywae]",
                                updatedList[it].wastage_ywae.toString()
                            )
                        )
                        rebuy_price_vertical_option.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][rebuy_price_vertical_option]",
                                updatedList[it].rebuy_price_vertical_option.orEmpty()
                            )
                        )
                        repeat(updatedList[it].productId.orEmpty().size) { index ->
                            productIdList.add(
                                MultipartBody.Part.createFormData(
                                    "old_stocks[$it][products][]",
                                    updatedList[it].productId.orEmpty()[index]
                                )
                            )
                        }
                        isEditable.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][is_editable]",
                                if (updatedList[it].isEditable) "1" else "0"

                            )
                        )
                        isChecked.add(
                            MultipartBody.Part.createFormData(
                                "old_stocks[$it][is_checked]",
                                if (updatedList[it].isChecked) "1" else "0"
                            )
                        )
                    }
                }
                _updateStockFromHomeInfoLiveData.value = Resource.Loading()
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
    }

    var selectedImagePath: String? = null

}