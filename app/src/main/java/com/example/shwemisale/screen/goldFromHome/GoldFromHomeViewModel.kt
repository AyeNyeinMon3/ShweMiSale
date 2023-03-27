package com.example.shwemisale.screen.goldFromHome

import androidx.lifecycle.*
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockWeightByVoucherDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.asUiModel
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo
import com.example.shwemisale.room_database.entity.asEntity
import com.example.shwemisale.room_database.entity.asUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GoldFromHomeViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val appDatabase: AppDatabase,
    private val localDatabase: LocalDatabase
) : ViewModel() {

    fun saveTotalPawnPrice(price:String){
        localDatabase.saveTotalPawnPriceForStockFromHome(price)
    }
    fun saveTotalGoldWeightYwae(ywae:String){
        localDatabase.saveGoldWeightYwaeForStockFromHome(ywae)
    }
    fun saveTotalCVoucherBuyingPrice(price:String){
        localDatabase.saveTotalCVoucherBuyingPriceForStockFromHome(price)
    }

    private val _stockWeightByVoucherLiveData =
        MutableLiveData<Resource<List<StockWeightByVoucherUiModel>>>()
    val stockWeightByVoucherLiveData: LiveData<Resource<List<StockWeightByVoucherUiModel>>>
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

    fun getStockFromHomeList() {
        _stockFromHomeInfoLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _stockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.getStockFromHomeList(localDatabase.getStockFromHomeSessionKey().orEmpty())
        }
    }

    private val _createStockFromHomeInfoLiveData =
        MutableLiveData<Resource<String>>()
    val createStockFromHomeInfoLiveData: LiveData<Resource<String>>
        get() = _createStockFromHomeInfoLiveData

    fun createStockFromHome(
        a_buying_price: String?,
        b_voucher_buying_value: String?,
        c_voucher_buying_price: String?,
        calculated_buying_value: String?,
        calculated_for_pawn: String?,
        d_gold_weight_ywae: String?,
        e_price_from_new_voucher: String?,
        f_voucher_shown_gold_weight_ywae: String?,
        gem_value: String?,
        gem_weight_details_qty: List<MultipartBody.Part?>?,
        gem_weight_details_gm: List<MultipartBody.Part?>?,
        gem_weight_details_ywae: List<MultipartBody.Part?>?,
        gem_weight_ywae: String?,
        gold_gem_weight_ywae: String?,
        gold_weight_ywae: String?,
        gq_in_carat: String?,
        has_general_expenses: String?,
        imageId: String?,
        imageFile: RequestBody?,
        impurities_weight_ywae: String?,
        maintenance_cost: String?,
        price_for_pawn: String?,
        pt_and_clip_cost: String?,
        qty: String?,
        rebuy_price: String?,
        size: String?,
        stock_condition: String?,
        stock_name: String?,
        type: String?,
        wastage_ywae: String?,
        rebuy_price_vertical_option:String?,

        ) {
        viewModelScope.launch {
            _createStockFromHomeInfoLiveData.value = Resource.Loading()
            _createStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.createStockFromHomeList(
                    a_buying_price,
                    b_voucher_buying_value,
                    c_voucher_buying_price,
                    calculated_buying_value,
                    calculated_for_pawn,
                    d_gold_weight_ywae,
                    e_price_from_new_voucher,
                    f_voucher_shown_gold_weight_ywae,
                    gem_value,
                    gem_weight_details_qty,
                    gem_weight_details_gm,
                    gem_weight_details_ywae,
                    gem_weight_ywae,
                    gold_gem_weight_ywae,
                    gold_weight_ywae,
                    gq_in_carat,
                    has_general_expenses,
                    imageId,
                    imageFile,
                    impurities_weight_ywae,
                    maintenance_cost,
                    price_for_pawn,
                    pt_and_clip_cost,
                    qty,
                    rebuy_price,
                    size,
                    stock_condition,
                    stock_name,
                    type,
                    wastage_ywae,
                    rebuy_price_vertical_option,
                    localDatabase.getStockFromHomeSessionKey().orEmpty()
                )
        }
    }

    private val _stockFromHomeInfoInVoucherLiveData =
        MutableLiveData<Resource<List<StockFromHomeDomain>>>()
    val stockFromHomeInfoInVoucherLiveData: LiveData<Resource<List<StockFromHomeDomain>>>
        get() = _stockFromHomeInfoInVoucherLiveData

    fun getStockInfoByVoucher(voucherCode: String, productIdList: List<String>) {
        viewModelScope.launch {
            _stockFromHomeInfoInVoucherLiveData.value = Resource.Loading()
            _stockFromHomeInfoInVoucherLiveData.value =
                goldFromHomeRepositoryImpl.getStockInfoByVoucher(voucherCode, productIdList)
        }
    }


    fun deleteStock(item: StockFromHomeDomain) {
        viewModelScope.launch {
            val updatedList = _stockFromHomeInfoLiveData.value?.data?.filter { it!=item }
            val a_buying_price =  mutableListOf<MultipartBody.Part>()
            val b_voucher_buying_value =  mutableListOf<MultipartBody.Part>()
            val c_voucher_buying_price =  mutableListOf<MultipartBody.Part>()
            val calculated_buying_value =  mutableListOf<MultipartBody.Part>()
            val calculated_for_pawn =  mutableListOf<MultipartBody.Part>()
            val d_gold_weight_ywae =  mutableListOf<MultipartBody.Part>()
            val e_price_from_new_voucher =  mutableListOf<MultipartBody.Part>()
            val f_voucher_shown_gold_weight_ywae =  mutableListOf<MultipartBody.Part>()
            val gem_value =  mutableListOf<MultipartBody.Part>()
            var gemQtyMultiPartList = mutableListOf<MultipartBody.Part?>()
            var gemWeightYwaeMultiPartList = mutableListOf<MultipartBody.Part?>()
            var gemWeightGmMultiPartList = mutableListOf<MultipartBody.Part?>()
            val gem_weight_ywae =  mutableListOf<MultipartBody.Part>()
            val gold_gem_weight_ywae =  mutableListOf<MultipartBody.Part>()
            val gold_weight_ywae =  mutableListOf<MultipartBody.Part>()
            val gq_in_carat =  mutableListOf<MultipartBody.Part>()
            val has_general_expenses =  mutableListOf<MultipartBody.Part>()
            val imageId =  mutableListOf<MultipartBody.Part>()
            val imageFile =  mutableListOf<MultipartBody.Part>()
            val impurities_weight_ywae =  mutableListOf<MultipartBody.Part>()
            val maintenance_cost =  mutableListOf<MultipartBody.Part>()
            val price_for_pawn =  mutableListOf<MultipartBody.Part>()
            val pt_and_clip_cost =  mutableListOf<MultipartBody.Part>()
            val qty =  mutableListOf<MultipartBody.Part>()
            val rebuy_price =  mutableListOf<MultipartBody.Part>()
            val size =  mutableListOf<MultipartBody.Part>()
            val stock_condition =  mutableListOf<MultipartBody.Part>()
            val stock_name =  mutableListOf<MultipartBody.Part>()
            val type =  mutableListOf<MultipartBody.Part>()
            val wastage_ywae =  mutableListOf<MultipartBody.Part>()
            val rebuy_price_vertical_option =  mutableListOf<MultipartBody.Part>()
            if (updatedList != null){
                if (updatedList.isNotEmpty()){
                    repeat(updatedList.size){
                        val gemQtyList = updatedList[it].gem_weight_details.orEmpty().map { it.gem_qty }
                        val gemWeightYwaeList = updatedList[it].gem_weight_details.orEmpty().map { it.gem_weight_ywae_per_unit }
                        val gemWeightGmList = updatedList[it].gem_weight_details.orEmpty().map { it.gem_weight_gm_per_unit }

                        repeat(updatedList[it].gem_weight_details.orEmpty().size){
                            gemQtyMultiPartList.add(
                                MultipartBody.Part.createFormData(
                                    "gem_weight_details[$it][gem_qty]",
                                    gemQtyList[it]
                                )
                            )
                            gemWeightYwaeMultiPartList.add(
                                MultipartBody.Part.createFormData(
                                    "gem_weight_details[$it][gem_weight_ywae_per_unit]",
                                    gemWeightYwaeList[it]
                                )
                            )
                            gemWeightGmMultiPartList.add(
                                MultipartBody.Part.createFormData(
                                    "gem_weight_details[$it][gem_weight_gm_per_unit]",
                                    gemWeightGmList[it]
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
                        updatedList[it].image?.id?.let {id->
                            imageId.add(
                                MultipartBody.Part.createFormData(
                                    "old_stocks[$it][imageId]",
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
                                updatedList[it].rebuy_price_vertical_option.toString()
                            )
                        )
                    }
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
                        rebuy_price_vertical_option =rebuy_price_vertical_option,
                        sessionKey = localDatabase.getStockFromHomeSessionKey().orEmpty()
                    )
                }else{
                    normalSaleRepositoryImpl.updateStockFromHomeList(
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
                        null,
                        localDatabase.getStockFromHomeSessionKey().orEmpty(),
                    )
                }
            }

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


    private val _oldVoucherPaidAmountLiveData = MutableLiveData<Resource<String>>()
    val oldVoucherPaidAmountLiveData: LiveData<Resource<String>>
        get() = _oldVoucherPaidAmountLiveData

    fun getOldVoucherPaidAmount(voucherCode: String, id: String) {
        _oldVoucherPaidAmountLiveData.value = Resource.Loading()
        viewModelScope.launch {
            val result = normalSaleRepositoryImpl.getPaidAmountOfVoucher(voucherCode)
            when (result) {
                is Resource.Success -> {
                    _oldVoucherPaidAmountLiveData.value = Resource.Success(result.data)
                }
                is Resource.Error -> {
                    _oldVoucherPaidAmountLiveData.value = Resource.Error(result.message)
                }
                else -> {}
            }
        }
    }

}