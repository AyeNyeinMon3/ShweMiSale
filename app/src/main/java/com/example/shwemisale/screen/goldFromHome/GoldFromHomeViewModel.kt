package com.example.shwemisale.screen.goldFromHome

import androidx.lifecycle.*
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockWeightByVoucherDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.asUiModel
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintDto
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintResponse
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.AuthRepoImpl
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.repositoryImpl.PrintingRepoImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo
import com.example.shwemisale.room_database.entity.asEntity
import com.example.shwemisale.room_database.entity.asUiModel
import com.example.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GoldFromHomeViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val authRepoImpl: AuthRepoImpl,
    private val appDatabase: AppDatabase,
    private val localDatabase: LocalDatabase,
    private val printingRepoImpl: PrintingRepoImpl
) : ViewModel() {
    var checkedProductIdFromVoucher = mutableListOf<String>()
    private val _updateStockFromHomeInfoLiveData =
        SingleLiveEvent<Resource<String>>()
    val updateStockFromHomeInfoLiveData: SingleLiveEvent<Resource<String>>
        get() = _updateStockFromHomeInfoLiveData

    fun saveTotalPawnPrice(price: String) {
        localDatabase.saveTotalPawnPriceForStockFromHome(price)
    }

    fun saveTotalGoldWeightYwae(ywae: String) {
        localDatabase.saveGoldWeightYwaeForStockFromHome(ywae)
    }

    fun saveVoucherBuyingPriceForPawn(price: String) {
        localDatabase.saveTotalVoucherBuyingPriceForPawn(price)
    }
    fun savePawnPriceForRemainedPawnItems(price: String) {
        localDatabase.saveRemainedPawnItemsPrice(price)
    }
    fun saveTotalCVoucherBuyingPrice(price: String) {
        localDatabase.saveTotalCVoucherBuyingPriceForStockFromHome(price)
    }

    fun getCurrentUserName():String {
        return localDatabase.getCurrentSalesPersonName()
    }
    fun removeTotalPawnPrice() {
        localDatabase.removeTotalPawnPriceForStockFromHome()
    }

    fun removeTotalGoldWeightYwae() {
        localDatabase.removeGoldWeightYwaeForStockFromHome()
    }

    fun removeTotalCVoucherBuyingPrice() {
        localDatabase.removeTotalCVoucherBuyingPriceForStockFromHome()
    }
    private val _logoutLiveData=SingleLiveEvent<Resource<String>>()
    val logoutLiveData:SingleLiveEvent<Resource<String>>
        get()=_logoutLiveData

    fun logout(){
        _logoutLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _logoutLiveData.value = authRepoImpl.logout()
        }
    }

    private val _printRebuyLiveData=SingleLiveEvent<Resource<RebuyPrintDto>>()
    val printRebuyLiveData:SingleLiveEvent<Resource<RebuyPrintDto>>
        get()=_printRebuyLiveData

    fun printRebuy(rebuyId:String){
        _printRebuyLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _printRebuyLiveData.value = printingRepoImpl.getRebuyPrint(rebuyId)
        }
    }

    private val _stockWeightByVoucherLiveData =
        SingleLiveEvent<Resource<List<StockWeightByVoucherUiModel>>>()
    val stockWeightByVoucherLiveData: SingleLiveEvent<Resource<List<StockWeightByVoucherUiModel>>>
        get() = _stockWeightByVoucherLiveData

//    fun resetstockWeightByVoucherLiveData() {
//        _stockWeightByVoucherLiveData.value = null
//    }

    fun getStockWeightByVoucher(voucherCode: String) {
        _stockWeightByVoucherLiveData.value = Resource.Loading()
        viewModelScope.launch {
            val result = goldFromHomeRepositoryImpl.getStockWeightByVoucher(voucherCode)
            when (result) {
                is Resource.Success -> {

                    _stockWeightByVoucherLiveData.value =
                        Resource.Success(result.data!!.map { it.asUiModel() })
                }

                is Resource.Error -> {
                    _stockWeightByVoucherLiveData.value = Resource.Error(result.message)
                }

                else -> {}
            }
        }
    }


    private val _stockFromHomeInfoLiveData = MutableLiveData<Resource<List<StockFromHomeDomain>>>()
    val stockFromHomeInfoLiveData: LiveData<Resource<List<StockFromHomeDomain>>>
        get() = _stockFromHomeInfoLiveData
    private val _pawnStockFromHomeInfoLiveData =
        SingleLiveEvent<Resource<List<StockFromHomeDomain>>>()
    val pawnStockFromHomeInfoLiveData: SingleLiveEvent<Resource<List<StockFromHomeDomain>>>
        get() = _pawnStockFromHomeInfoLiveData

    fun getStockFromHomeList(isPawn: Boolean, buttonBehavior: String?) {
        _stockFromHomeInfoLiveData.value = Resource.Loading()
        viewModelScope.launch {
            val sessionKey = if (isPawn) {
                localDatabase.getPawnOldStockSessionKey().orEmpty()
            } else {
                localDatabase.getStockFromHomeSessionKey().orEmpty()
            }
            _stockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.getStockFromHomeList(sessionKey)
        }
    }

    fun getStockFromHomeForPawnList(pawnVoucherCode: String,isPawnSale:Boolean) {
        _pawnStockFromHomeInfoLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _pawnStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.getStockFromHomeForPawn(pawnVoucherCode,if (isPawnSale) "1" else "0")
        }
    }


    private val _createStockFromHomeInfoLiveData =
        SingleLiveEvent<Resource<String>>()
    val createStockFromHomeInfoLiveData: SingleLiveEvent<Resource<String>>
        get() = _createStockFromHomeInfoLiveData


    fun createStockFromHome(
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
            val a_buying_price = MultipartBody.Part.createFormData("a_buying_price", a_buying_price)
            val imageId =imageId?.let {MultipartBody.Part.createFormData("image[id]", it)  }
            val b_voucher_buying_value =
                MultipartBody.Part.createFormData("b_voucher_buying_value", b_voucher_buying_value)
            val c_voucher_buying_price =
                MultipartBody.Part.createFormData("c_voucher_buying_price", c_voucher_buying_price)
            val calculated_buying_value =
                MultipartBody.Part.createFormData("calculated_buying_value", calculated_buying_value)
            val calculated_for_pawn =
                MultipartBody.Part.createFormData("calculated_for_pawn", calculated_for_pawn)
            val d_gold_weight_ywae = MultipartBody.Part.createFormData("d_gold_weight_ywae", d_gold_weight_ywae)
            val e_price_from_new_voucher =
                MultipartBody.Part.createFormData("e_price_from_new_voucher", e_price_from_new_voucher)
            val f_voucher_shown_gold_weight_ywae =
                MultipartBody.Part.createFormData("f_voucher_shown_gold_weight_ywae", f_voucher_shown_gold_weight_ywae)
            val gem_value = MultipartBody.Part.createFormData("gem_value", gem_value)

            val gem_weight_ywae = MultipartBody.Part.createFormData("gem_weight_ywae", gem_weight_ywae)
            val gold_gem_weight_ywae =
                MultipartBody.Part.createFormData("gold_gem_weight_ywae", gold_gem_weight_ywae)
            val gold_weight_ywae = MultipartBody.Part.createFormData("gold_weight_ywae", gold_weight_ywae)
            val gq_in_carat = MultipartBody.Part.createFormData("gq_in_carat", gq_in_carat)
            val has_general_expenses =
                MultipartBody.Part.createFormData("has_general_expenses", has_general_expenses)
            val impurities_weight_ywae =
                MultipartBody.Part.createFormData("impurities_weight_ywae", impurities_weight_ywae)
            val maintenance_cost = MultipartBody.Part.createFormData("maintenance_cost", maintenance_cost)
            val price_for_pawn = MultipartBody.Part.createFormData("price_for_pawn", price_for_pawn)
            val pt_and_clip_cost = MultipartBody.Part.createFormData("pt_and_clip_cost", pt_and_clip_cost)
            val qty = MultipartBody.Part.createFormData("qty", qty)
            val rebuy_price = MultipartBody.Part.createFormData("rebuy_price", rebuy_price)
            val size = MultipartBody.Part.createFormData("size", size)
            val stock_condition = MultipartBody.Part.createFormData("stock_condition", stock_condition)
            val stock_name = MultipartBody.Part.createFormData("stock_name", stock_name)
            val type = MultipartBody.Part.createFormData("type", type)
            val wastage_ywae = MultipartBody.Part.createFormData("wastage_ywae", wastage_ywae)
            val rebuy_price_vertical_option =
                MultipartBody.Part.createFormData("rebuy_price_vertical_option", rebuy_price_vertical_option)
            val productIdListMultipartBody = mutableListOf<MultipartBody.Part>()
            productIdList?.forEach {
                productIdListMultipartBody.add(
                    MultipartBody.Part.createFormData(
                        "products[]",
                        it
                    )
                )
            }
            val isEditableMultiPart = MultipartBody.Part.createFormData("is_editable", if (isEditable)"1" else "0")
            val isCheckedMultiPart = MultipartBody.Part.createFormData("is_checked", if (isChecked)"1" else "0")
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
                    imageFile = null,
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

    private val _stockFromHomeInfoInVoucherLiveData =
        SingleLiveEvent<Resource<List<StockFromHomeDomain>>>()
    val stockFromHomeInfoInVoucherLiveData: SingleLiveEvent<Resource<List<StockFromHomeDomain>>>
        get() = _stockFromHomeInfoInVoucherLiveData

    fun getStockInfoByVoucher(voucherCode: String, productIdList: List<String>) {
        viewModelScope.launch {
            _stockFromHomeInfoInVoucherLiveData.value = Resource.Loading()
            _stockFromHomeInfoInVoucherLiveData.value =
                goldFromHomeRepositoryImpl.getStockInfoByVoucher(voucherCode, productIdList,goldPrice18KId)
        }
    }

    private val _deleteStockLiveData =
        SingleLiveEvent<Resource<String>>()
    val deleteStockLiveData: SingleLiveEvent<Resource<String>>
        get() = _deleteStockLiveData

    fun deleteStock(oldStockId:String) {
        viewModelScope.launch {
            _deleteStockLiveData.value=Resource.Loading()
            _deleteStockLiveData.value = normalSaleRepositoryImpl.deleteOldStock(oldStockId)
        }
    }


    fun updateStockFromHomeInfoFinal(
        finalPawnPrice: String,
        finalGoldWeightY: String,
        finalVoucherPaidAmount: String
    ) {
        viewModelScope.launch {
            appDatabase.stockFromHomeFinalInfoDao.updateStockFromHomeFinalInfo(
                StockFromHomeFinalInfo(1L, finalPawnPrice, finalGoldWeightY, finalVoucherPaidAmount)
            )
        }
    }


//    private val _oldVoucherPaidAmountLiveData = MutableLiveData<Resource<String>>()
//    val oldVoucherPaidAmountLiveData: LiveData<Resource<String>>
//        get() = _oldVoucherPaidAmountLiveData
//
//    fun getOldVoucherPaidAmount(voucherCode: String, id: String) {
//        _oldVoucherPaidAmountLiveData.value = Resource.Loading()
//        viewModelScope.launch {
//            val result = normalSaleRepositoryImpl.getPaidAmountOfVoucher(voucherCode)
//            when (result) {
//                is Resource.Success -> {
//                    _oldVoucherPaidAmountLiveData.value = Resource.Success(result.data)
//                }
//
//                is Resource.Error -> {
//                    _oldVoucherPaidAmountLiveData.value = Resource.Error(result.message)
//                }
//
//                else -> {}
//            }
//        }
//    }

    private val _buyStockLiveData = SingleLiveEvent<Resource<String>>()
    val buyStockLiveData: SingleLiveEvent<Resource<String>>
        get() = _buyStockLiveData

    fun buyOldStock() {
        _buyStockLiveData.value = Resource.Loading()
        viewModelScope.launch {
            val result = normalSaleRepositoryImpl.buyOldStock()
            when (result) {
                is Resource.Success -> {
                    _buyStockLiveData.value = Resource.Success(result.data)
                }

                is Resource.Error -> {
                    _buyStockLiveData.value = Resource.Error(result.message)
                }

                else -> {}
            }
        }
    }

    var goldPrice18KId = ""
    private val _goldTypePriceLiveData = MutableLiveData<Resource<List<GoldTypePriceDto>>>()
    val goldTypePriceLiveData: LiveData<Resource<List<GoldTypePriceDto>>>
        get() = _goldTypePriceLiveData

    fun getGoldTypePrice() {
        _goldTypePriceLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _goldTypePriceLiveData.value = goldFromHomeRepositoryImpl.getGoldType(null)
        }
    }


    fun updateStockFromHome(
        isChecked: Boolean,
        id:String,

    ) {
        viewModelScope.launch {
            val sessionKey = localDatabase.getPawnOldStockSessionKey()


            val isCheckedMultipartBody = MultipartBody.Part.createFormData("is_checked", if(isChecked) "1" else "0")

            _updateStockFromHomeInfoLiveData.value = Resource.Loading()
            _updateStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.updateStockFromHomeList(
                    MultipartBody.Part.createFormData("id",id),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sessionKey = sessionKey,
                    isChecked = isCheckedMultipartBody,
                    isPawn = true
                )
        }
    }

}


