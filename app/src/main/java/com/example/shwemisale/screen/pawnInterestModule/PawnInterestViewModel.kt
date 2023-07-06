package com.example.shwemisale.screen.pawnInterestModule

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.PawnVoucherScanDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateDto
import com.example.shwemisale.data_layers.dto.pawn.PawnVoucherScanDto
import com.example.shwemisale.data_layers.dto.printing.PawnCreatePrintDto
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.AuthRepoImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.repositoryImpl.PawnRepositoryImpl
import com.example.shwemisale.repositoryImpl.PrintingRepoImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class PawnInterestViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val pawnRepositoryImpl: PawnRepositoryImpl,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase,
    private val authRepoImpl: AuthRepoImpl,
    private val printingRepoImpl: PrintingRepoImpl
) : ViewModel() {
    var pawnData: PawnVoucherScanDomain? = null
    private val _createStockFromHomeInfoLiveData =
        SingleLiveEvent<Resource<String>>()
    val createStockFromHomeInfoLiveData: SingleLiveEvent<Resource<String>>
        get() = _createStockFromHomeInfoLiveData

    private val _logoutLiveData=SingleLiveEvent<Resource<String>>()
    val logoutLiveData:SingleLiveEvent<Resource<String>>
        get()=_logoutLiveData

    fun logout(){
        _logoutLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _logoutLiveData.value = authRepoImpl.logout()
        }
    }
    private val _pdfDownloadLiveData = SingleLiveEvent<Resource<String>>()
    val pdfDownloadLiveData: SingleLiveEvent<Resource<String>>
        get() = _pdfDownloadLiveData

    fun getPdf(pawnId:String){
        viewModelScope.launch {
            _pdfDownloadLiveData.value = Resource.Loading()
            _pdfDownloadLiveData.value=printingRepoImpl.getPawnPrint(pawnId)
        }
    }
    private val _printPawnSaleLiveData=SingleLiveEvent<Resource<String>>()
    val printPawnSaleLiveData:SingleLiveEvent<Resource<String>>
        get()=_printPawnSaleLiveData

    fun printPawnSale(pawnSaleId:String){
        _printPawnSaleLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _printPawnSaleLiveData.value = printingRepoImpl.getPawnPrint(pawnSaleId)
        }
    }

    private val _getPawnVoucherScanLiveData = SingleLiveEvent<Resource<PawnVoucherScanDto>>()
    val getPawnVoucherScanLiveData: SingleLiveEvent<Resource<PawnVoucherScanDto>>
        get() = _getPawnVoucherScanLiveData
    fun getTotalPawnPrice():String {
        return localDatabase.getTotalPawnPriceForStockFromHome()
    }

    fun getTotalVoucherBuyingPrice():String {
        return localDatabase.getTotalVoucherBuyingPriceForPawn()
    }
    fun getPawnPriceForRemainedPawnItem():String {
        return localDatabase.getRemainedPawnItemsPrice()
    }

    fun pawnVoucherScan(voucherCode: String) {
        viewModelScope.launch {
            _getPawnVoucherScanLiveData.value = Resource.Loading()
            _getPawnVoucherScanLiveData.value = pawnRepositoryImpl.getPawnVoucherScan(voucherCode)
        }
    }

    private val _createPrepaidDebtLiveData = SingleLiveEvent<Resource<String>>()
    val createPrepaidDebtLiveData: LiveData<Resource<String>>
        get() = _createPrepaidDebtLiveData

    fun createPrepaidDebt(
        voucherCode: String,
        prepaid_debt: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _createPrepaidDebtLiveData.value = Resource.Loading()
            _createPrepaidDebtLiveData.value =
                pawnRepositoryImpl.createPrepaidDebt(
                    voucherCode,
                    prepaid_debt,
                    reduced_amount,
                    is_app_functions_allowed
                )
        }
    }

    private val _createPrepaidInterestLiveData = SingleLiveEvent<Resource<String>>()
    val createPrepaidInterestLiveData: LiveData<Resource<String>>
        get() = _createPrepaidInterestLiveData

    fun createPrepaidInterest(
        voucherCode: String,
        number_of_months: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _createPrepaidInterestLiveData.value = Resource.Loading()
            _createPrepaidInterestLiveData.value =
                pawnRepositoryImpl.createPrepaidInterest(
                    voucherCode,
                    number_of_months,
                    reduced_amount,
                    is_app_functions_allowed
                )
        }
    }

    private val _increaseDebtLiveData = SingleLiveEvent<Resource<String>>()
    val increaseDebtLiveData: SingleLiveEvent<Resource<String>>
        get() = _increaseDebtLiveData

    fun increaseDebt(
        voucherCode: String,
        increased_debt: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,
    ) {
        viewModelScope.launch {
            _increaseDebtLiveData.value = Resource.Loading()
            _increaseDebtLiveData.value = pawnRepositoryImpl.increaseDebt(
                voucherCode,
                increased_debt,
                reduced_amount,
                is_app_functions_allowed,
                localDatabase.getPawnOldStockSessionKey().orEmpty()
            )
        }
    }

    private val _payInterestAndIncreaseDebtLiveData = SingleLiveEvent<Resource<String>>()
    val payInterestAndIncreaseDebtLiveData: SingleLiveEvent<Resource<String>>
        get() = _payInterestAndIncreaseDebtLiveData

    fun payInterestAndIncreaseDebt(
        voucherCode: String,
        increased_debt: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _payInterestAndIncreaseDebtLiveData.value = Resource.Loading()
            _payInterestAndIncreaseDebtLiveData.value =
                pawnRepositoryImpl.payInterestAndIncreaseDebt(
                    voucherCode,
                    increased_debt,
                    reduced_amount,
                    is_app_functions_allowed,
                    localDatabase.getPawnOldStockSessionKey().orEmpty()
                )
        }
    }

    private val _payInterestLiveData = SingleLiveEvent<Resource<String>>()
    val payInterestLiveData: SingleLiveEvent<Resource<String>>
        get() = _payInterestLiveData

    fun payInterest(
        voucherCode: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _payInterestLiveData.value = Resource.Loading()
            _payInterestLiveData.value =
                pawnRepositoryImpl.payInterest(
                    voucherCode,
                    reduced_amount,
                    is_app_functions_allowed
                )
        }
    }

    private val _payInterestAndSettleDebtLiveData = SingleLiveEvent<Resource<String>>()
    val payInterestAndSettleDebtLiveData: SingleLiveEvent<Resource<String>>
        get() = _payInterestAndSettleDebtLiveData

    fun payInterestAndSettleDebt(
        voucherCode: String,
        reduced_amount: String,
        debt: String,
        is_app_functions_allowed: String?
    ) {
        viewModelScope.launch {
            _payInterestAndSettleDebtLiveData.value = Resource.Loading()
            _payInterestAndSettleDebtLiveData.value =
                pawnRepositoryImpl.payInterestAndSettleDebt(
                    voucherCode,
                    reduced_amount,
                    debt,
                    is_app_functions_allowed
                )
        }
    }

    private val _payInterestAndReturnStockLiveData = SingleLiveEvent<Resource<String>>()
    val payInterestAndReturnStockLiveData: SingleLiveEvent<Resource<String>>
        get() = _payInterestAndReturnStockLiveData

    fun payInterestAndReturnStock(
        voucherCode: String,
        reduced_amount: String,
        debt: String,
        old_stock_id: List<String>,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _payInterestAndReturnStockLiveData.value = Resource.Loading()
            _payInterestAndReturnStockLiveData.value =
                pawnRepositoryImpl.payInterestAndReturnStock(
                    voucherCode,
                    reduced_amount,
                    debt,
                    is_app_functions_allowed,
                    old_stock_id
                )
        }
    }

    private val _settleLiveData = SingleLiveEvent<Resource<String>>()
    val settleLiveData: SingleLiveEvent<Resource<String>>
        get() = _settleLiveData

    fun settle(
        voucherCode: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,

        ) {
        viewModelScope.launch {
            _settleLiveData.value = Resource.Loading()
            _settleLiveData.value =
                pawnRepositoryImpl.settle(voucherCode, reduced_amount, is_app_functions_allowed)
        }
    }

    private val _sellOldStockLiveData = SingleLiveEvent<Resource<String>>()
    val sellOldStockLiveData: SingleLiveEvent<Resource<String>>
        get() = _sellOldStockLiveData

    fun sellOldStock(
        voucherCode: String,
        reduced_amount: String,
        is_app_functions_allowed: String?,
    ) {
        viewModelScope.launch {
            _sellOldStockLiveData.value = Resource.Loading()
            _sellOldStockLiveData.value =
                pawnRepositoryImpl.sellOldStock(
                    voucherCode,
                    reduced_amount,
                    is_app_functions_allowed,
                    localDatabase.getPawnOldStockSessionKey().orEmpty()
                )
        }
    }

    private val _pawnStockFromHomeInfoLiveData =
        SingleLiveEvent<Resource<List<StockFromHomeDomain>>>()
    val pawnStockFromHomeInfoLiveData: SingleLiveEvent<Resource<List<StockFromHomeDomain>>>
        get() = _pawnStockFromHomeInfoLiveData

    fun getStockFromHomeForPawnList(pawnVoucherCode: String) {
        _pawnStockFromHomeInfoLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _pawnStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.getStockFromHomeForPawn(pawnVoucherCode)
        }
    }

    fun createStockFromHome(
        itemList: List<StockFromHomeDomain>,
        isPawn: Boolean
    ) {
        viewModelScope.launch {
            val updatedList = itemList
            val pawnOldStockId = mutableListOf<MultipartBody.Part>()
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
                if (isPawn) {
                    pawnOldStockId.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][id]",
                            updatedList[it].id.toString()
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
//                var rebuyPrice = if (updatedList[it].derived_gold_type_id == goldPrice18KId) {
//                    updatedList[it].rebuy_price.toInt() * 16.6
//                } else {
//                    updatedList[it].rebuy_price.toInt()
//                }
                rebuy_price.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][rebuy_price]",
                        updatedList[it].rebuy_price.toInt()
                            .toString()
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
                        "0"
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
                localDatabase.getPawnOldStockSessionKey().orEmpty()
            } else {
                localDatabase.getStockFromHomeSessionKey().orEmpty()
            }
            _createStockFromHomeInfoLiveData.value = Resource.Loading()
            _createStockFromHomeInfoLiveData.value =
                normalSaleRepositoryImpl.createStockFromHomeList(
                    id = pawnOldStockId,
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
}