package com.example.shwemisale.screen.oldStockDetail

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemi.util.compressImage
import com.example.shwemi.util.generateNumberFromEditText
import com.example.shwemi.util.getRoundDownForPrice
import com.example.shwemi.util.handleInfinity
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemWithSize
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.screen.goldFromHome.getGramFromYwae
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.example.shwemisale.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.DataOutput
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class OldStockDetailViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val appDatabase: AppDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase
) : ViewModel() {
    private val _imagePathLiveData = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String>
        get() = _imagePathLiveData

    fun setImagePathLiveData(imagePath: String) {
        _imagePathLiveData.value = imagePath
    }

    private val _nameTagLiveData = MutableLiveData<String>()
    val nameTagLiveData: LiveData<String>
        get() = _nameTagLiveData

    fun setNameTag(nameTag: String) {
        _nameTagLiveData.value = nameTag
    }

    private val _totalQtyLiveData = MutableLiveData<String>()
    val totalQtyLiveData: LiveData<String>
        get() = _totalQtyLiveData

    fun setTotalQty(totalQty: String) {
        _totalQtyLiveData.value = totalQty
    }

    private val _wastageYwaeLiveData = MutableLiveData<Double>()
    val wastageYwaeLiveData: LiveData<Double>
        get() = _wastageYwaeLiveData

    fun setWastageYwae(wastageYwae: Double) {
        _wastageYwaeLiveData.value = wastageYwae
    }

    private val _maintainenceCostLiveData = MutableLiveData<Int>(0)
    val maintainenceCostLiveData: LiveData<Int>
        get() = _maintainenceCostLiveData

    fun setMaintainenceCost(maintainenceCost: Int) {
        _maintainenceCostLiveData.value = maintainenceCost
    }

    private val _ptClipCostLiveData = MutableLiveData<Int>(0)
    val ptClipCostLiveData: LiveData<Int>
        get() = _ptClipCostLiveData

    fun setPtClipCost(ptClipCost: Int) {
        _ptClipCostLiveData.value = ptClipCost
    }

    private val _horizontalOptionLiveData = MutableLiveData<String>()
    val horizontalOptionLiveData: LiveData<String>
        get() = _horizontalOptionLiveData

    fun setHorizontalOption(horizontalOption: String) {
        _horizontalOptionLiveData.value = horizontalOption
    }

    private val _verticalOptionLiveData = MutableLiveData<String>()
    val verticalOptionLiveData: LiveData<String>
        get() = _verticalOptionLiveData

    fun setVerticalOption(verticalOption: String) {
        _verticalOptionLiveData.value = verticalOption
    }

    private val _sizeLiveData = MutableLiveData<String>()
    val sizeLiveData: LiveData<String>
        get() = _sizeLiveData

    fun setSize(size: String) {
        _sizeLiveData.value = size
    }


    private val _goldPriceLiveData = MutableLiveData<Int>(0)
    val goldPriceLiveData: LiveData<Int>
        get() = _goldPriceLiveData

    fun setGoldPrice(goldPrice: Int) {
        _goldPriceLiveData.value = goldPrice
    }

    private val _goldWeightYwaeLiveData = MutableLiveData<Double>(0.0)
    val goldWeightYwaeLiveData: LiveData<Double>
        get() = _goldWeightYwaeLiveData

    fun setGoldWeightYwaeOnlyValue(ywae:Double){
        _goldWeightYwaeLiveData.value = ywae
    }
    fun setGoldWeightYwae() {
        _goldWeightYwaeLiveData.value =
            _goldAndGemWeightYwaeLiveData.value!! - _gemWeightYwaeLiveData.value!! - _impurityWeightYwaeLiveData.value!!
    }

    var `goldPrice18K` = "0"
    private val _pawnDiffValueLiveData = MutableLiveData<String>("0")
    val pawnDiffValueLiveData: LiveData<String>
        get() = _pawnDiffValueLiveData

    fun setPawnDiffValue(pawnDiffValue: String) {
        _pawnDiffValueLiveData.value = pawnDiffValue
    }

    private val _goldAndGemWeightGmLiveData = MutableLiveData<Double>(0.0)
    val goldAndGemWeightGmLiveData: LiveData<Double>
        get() = _goldAndGemWeightGmLiveData

    fun setgoldAndgemWeightGm(goldAndGemWeightGm: Double) {
        val ywae = getYwaeFromGram(goldAndGemWeightGm)
        if (_goldAndGemWeightYwaeLiveData.value != ywae){
            setgoldAndGemWeightYwae(ywae)
        }
        _goldAndGemWeightGmLiveData.value = goldAndGemWeightGm
    }

    private val _goldAndGemWeightYwaeLiveData = MutableLiveData<Double>(0.0)
    val goldAndGemWeightYwaeLiveData: LiveData<Double>
        get() = _goldAndGemWeightYwaeLiveData

    fun setgoldAndGemWeightYwae(goldAndGemWeightYwae: Double) {
        val gram = getGramFromYwae(goldAndGemWeightYwae)
        if (_goldAndGemWeightGmLiveData.value != gram){
            setgoldAndgemWeightGm(gram)
        }
        _goldAndGemWeightYwaeLiveData.value = goldAndGemWeightYwae
    }

    private val _gemWeightYwaeLiveData = MutableLiveData<Double>(0.0)
    val gemWeightYwaeLiveData: LiveData<Double>
        get() = _gemWeightYwaeLiveData

    fun setgemWEightYwae(gemWEightYwae: Double) {
        _gemWeightYwaeLiveData.value = gemWEightYwae
    }

    private val _impurityWeightYwaeLiveData = MutableLiveData<Double>(0.0)
    val impurityWeightYwaeLiveData: LiveData<Double>
        get() = _impurityWeightYwaeLiveData

    fun setimpurityWeightYwae(impurityWeightYwae: Double) {
        _impurityWeightYwaeLiveData.value = impurityWeightYwae
    }

    private val _goldCaratLiveData = MutableLiveData<Double>(0.0)
    val goldCaratLiveData: LiveData<Double>
        get() = _goldCaratLiveData

    fun setgoldCarat(goldCarat: Double) {
        _goldCaratLiveData.value = goldCarat
    }

    private val _priceALiveData = MutableLiveData<Long>(0L)
    val priceALiveData: LiveData<Long>
        get() = _priceALiveData

    fun setpriceA(priceA: Long) {
        _priceALiveData.value = priceA
    }

    private val _priceBLiveData = MutableLiveData<Long>(0L)
    val priceBLiveData: LiveData<Long>
        get() = _priceBLiveData

    fun setpriceB(priceB: Long) {
        _priceBLiveData.value = priceB
    }

    private val _priceCLiveData = MutableLiveData<Long>(0L)
    val priceCLiveData: LiveData<Long>
        get() = _priceCLiveData

    fun setpriceC(priceC: Long) {
        _priceCLiveData.value = priceC
    }

    private val _dWeightYwaeInVoucherLiveData = MutableLiveData<Double>(0.0)
    val dWeightInVoucherYwaeLiveData: LiveData<Double>
        get() = _dWeightYwaeInVoucherLiveData

    fun setdWeightInVoucher(dWeightInVoucher: Double) {
        _dWeightYwaeInVoucherLiveData.value = dWeightInVoucher
    }

    private val _priceELiveData = MutableLiveData<Long>(0L)
    val priceELiveData: LiveData<Long>
        get() = _priceELiveData

    fun setpriceE(priceE: Long) {
        _priceELiveData.value = priceE
    }

    private val _fWeightYwaeLiveData = MutableLiveData<Double>(0.0)
    val fWeightYwaeLiveData: LiveData<Double>
        get() = _fWeightYwaeLiveData

    fun setfWeightYwae(fWeightYwae: Double) {
        _fWeightYwaeLiveData.value = fWeightYwae
    }

    private val _decidedPawnPriceLiveData = MutableLiveData<Long>(0L)
    val decidedPawnPriceLiveData: LiveData<Long>
        get() = _decidedPawnPriceLiveData

    fun setdecidedPawnPrice(decidedPawnPrice: Long) {
        _decidedPawnPriceLiveData.value = decidedPawnPrice
    }

    private val _calculatedPawnPiceLiveData = MutableLiveData<Long>(0L)
    val calculatedPawnPiceLiveData: LiveData<Long>
        get() = _calculatedPawnPiceLiveData

    fun setCalculatedPawnPrice(pawnPrice: Long) {
        _calculatedPawnPiceLiveData.value = pawnPrice
    }

    fun calculateRebuyPriceFromGoldCarat(gqCarat: Double): Pair<Int, Int> {
        val changedRebuyPrice =
            getRoundDownForPrice((gqCarat / 24 * _goldPriceLiveData.value!!).toInt())
        return Pair(_goldPriceLiveData.value!!, changedRebuyPrice)
    }

    fun calculateGoldQWhenRebuyPriceChange() {
        val currentRebuyPriceState = _rebuyPriceLiveData.value as? Resource.Success<Long>
        val gqInCarat =
            (currentRebuyPriceState?.data!!.toDouble() / _goldPriceLiveData.value!!) * 24
        setgoldCarat((gqInCarat * 100).roundToInt() / 100.0)
    }

    fun calculateDecidedPawnPrice() {
        val currentRebuyPriceState = _rebuyPriceLiveData.value as? Resource.Success<Long>
        val decidedPawnPrice =
            (currentRebuyPriceState?.data ?: 0L).toInt() - _pawnDiffValueLiveData.value!!.toInt()
        val decidedPawnPriceDecimal = getRoundDownForPrice(decidedPawnPrice).toString()
        setdecidedPawnPrice(decidedPawnPriceDecimal.toLong())
    }

    fun calculatePawnPrice(hasGeneralExpense: Boolean) {
        val goldKyat =
            (_goldAndGemWeightYwaeLiveData.value!! - _gemWeightYwaeLiveData.value!! - _impurityWeightYwaeLiveData.value!!) / 128
        val diamondGemValue = _gemValueLiveData.value!!

        val pawnPrice = if (hasGeneralExpense) {
            (goldKyat) * _decidedPawnPriceLiveData.value!! + diamondGemValue

        } else {
            (goldKyat) * _decidedPawnPriceLiveData.value!!
        }
        setCalculatedPawnPrice(getRoundDownForPrice(pawnPrice.toInt()).toLong())

    }

    fun calculateWhenAbuyingPriceChange(
        inputPriceA: Int,
        hasGeneralExpense: Boolean,
        wastageYwae: Double,
        ptClipCost: Int,
        maintenanceCost: Int,
        isSetPriceB:Boolean
    ): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val goldAndGemWeightYwae =
            if (_goldAndGemWeightYwaeLiveData.value != 0.0) _goldAndGemWeightYwaeLiveData.value!! else getYwaeFromGram(
                _goldAndGemWeightGmLiveData.value!!
            )
        val goldKyat =
            (goldAndGemWeightYwae - _gemWeightYwaeLiveData.value!! - _impurityWeightYwaeLiveData.value!!) / 128
        val wastageKyat = wastageYwae / 128
        val diamondGemValue = _gemValueLiveData.value!!

        val otherReducedCosts = diamondGemValue + maintenanceCost + ptClipCost
        val priceB = handleInfinity(
            if (hasGeneralExpense) {
                (goldKyat + wastageKyat) * inputPriceA + otherReducedCosts
            } else {
                (goldKyat) * inputPriceA
            }
        )

        val decidedPawnPrice =
            inputPriceA - _pawnDiffValueLiveData.value!!.toInt()
        val decidedPawnPriceDecimal = getRoundDownForPrice(decidedPawnPrice.toInt()).toString()
//        binding.edtDecidedPawnPrice.setText(decidedPawnPriceDecimal)
//        binding.edtPawnPrice.setText("")
//        binding.edtPriceB.setText(getRoundDownForPrice(priceB.toInt()).toString())

//        setdecidedPawnPrice(decidedPawnPriceDecimal.toLong())
//        setCalculatedPawnPrice(0L)
//        setpriceB(getRoundDownForPrice(priceB.toInt()).toLong())

        val fromToDecidedPawnPrice =
            Pair(_decidedPawnPriceLiveData.value!!.toInt(), decidedPawnPriceDecimal.toInt())
        val fromToBVouherPrice = Pair(_priceBLiveData.value!!.toInt(), priceB.toInt())


        if (isSetPriceB) setpriceB(getRoundDownForPrice(priceB.toInt()).toLong())
        return Pair(fromToDecidedPawnPrice, fromToBVouherPrice)

    }

    fun fValueChanged() {
        //f value will changed because b valued changed
        if (_priceELiveData.value!! != 0L) {
            var fywae =
                (_priceBLiveData.value!!.toDouble() / _priceELiveData.value!!.toDouble()) * 128
            setfWeightYwae(fywae)
        }
    }

    fun calculateWhenBbuyingPriceChange(
        priceB: Int,
        hasGeneralExpense: Boolean,
        wastageYwae: Double,
        ptClipCost: Int,
        maintenanceCost: Int
    ): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val goldKyat =
            (_goldAndGemWeightYwaeLiveData.value!! - _gemWeightYwaeLiveData.value!! - _impurityWeightYwaeLiveData.value!!) / 128
        val wastageKyat = wastageYwae / 128
        val diamondGemValue = _gemValueLiveData.value!!

        val otherReducedCosts =
            diamondGemValue + maintenanceCost + ptClipCost

        val priceA = if (hasGeneralExpense) {
            (priceB - otherReducedCosts.toLong()) / (goldKyat + wastageKyat)

        } else {
            (priceB) / goldKyat
        }
        val decidedPawnPrice =
            handleInfinity(priceA) - _pawnDiffValueLiveData.value!!.toInt()
        val decidedPawnPriceDecimal = getRoundDownForPrice(decidedPawnPrice.toInt())
        val fromToDecidedPawnPrice =
            Pair(_decidedPawnPriceLiveData.value!!.toInt(), decidedPawnPriceDecimal)
        val fromToAVouherPrice = Pair(_priceALiveData.value!!.toInt(), priceA.toInt())

        return Pair(fromToDecidedPawnPrice, fromToAVouherPrice)
    }

    fun calculateWhenCbuyingPriceChange(
        priceC: Int,
        hasGeneralExpense: Boolean,
        wastageYwae: Double,
        ptClipCost: Int,
        maintenanceCost: Int
    ): Pair<Double, Double> {
        val priceB = _priceBLiveData.value!!
        val wastageKyat = wastageYwae / 128
        val diamondGemValue = _gemValueLiveData.value!!

        val otherReducedCosts =
            diamondGemValue + maintenanceCost + ptClipCost
        val weightDYwae = if (hasGeneralExpense) {
            ((priceB.toDouble() - otherReducedCosts.toDouble()) / priceC.toDouble()) * 128 - wastageYwae
        } else {
            ((priceB.toDouble()) / priceC) * 128
        }

        return Pair(_dWeightYwaeInVoucherLiveData.value!!, weightDYwae)

    }

    fun calculateWhenFWeightYwaeChanged(
        fWeightYwae: Double,
        changeWastage: (Pair<Double,Double>) -> Unit,
        changeImpurity: (Pair<Double,Double>) -> Unit
    ) {
        setRebuyPrice(_priceELiveData.value!!.toLong())
        //
        val fKyat = fWeightYwae / 128
        val paymentFromShop =
            (_priceELiveData.value!! * fKyat).toInt()
        //ထည့်ပေးအရော့= [(calculated ဆိုင်မှ၀ယ်ပေးငွေ- လက်ခ- ကျောက်စိန်ဖိုး- PT, ကလစ်ဖိုး)/ Rebuy price]KPY - Gold wt KPY
        val diamondGemValue = _gemValueLiveData.value!!

        val otherReducedCosts =
            diamondGemValue + _maintainenceCostLiveData.value!! + _ptClipCostLiveData.value!!
        val goldWeightYwae = _goldWeightYwaeLiveData.value!!
        val buyPriceFromShop = if (_priceELiveData.value!! != 0L) _priceELiveData.value!! else _rebuyPriceFromShopLiveData.value!!
        val wastageYwae =
            handleInfinity((((paymentFromShop - otherReducedCosts) / buyPriceFromShop) * 128) - goldWeightYwae)
        if (wastageYwae > 0.0 && wastageYwae.isNaN().not()) {
            changeWastage(Pair(_wastageYwaeLiveData.value!!,wastageYwae))
        } else {
            val impuritiesWeight = (wastageYwae * (-1)) + _impurityWeightYwaeLiveData.value!!
            changeImpurity(Pair(_impurityWeightYwaeLiveData.value!!,impuritiesWeight))
        }
    }


    private val _gemValueLiveData = MutableLiveData<Int>(0)
    val gemValueLiveData: LiveData<Int>
        get() = _gemValueLiveData

    fun setgemValue(gemValue: Int) {
        _gemValueLiveData.value = gemValue
    }

    fun getSelectedStockType(): Pair<String, Int> {
        val currentStateRebuyItem =
            _rebuyItemeLiveData.value as? Resource.Success<RebuyItemWithSize>
        var totalqty = 0
        var name = ""

        if (_sizeLiveData.value == "small") {

            val rebuyItemList =
                currentStateRebuyItem?.data?.smallSizeItems?.filter { it.qty > 0 }.orEmpty()
            rebuyItemList.forEach {
                totalqty += it.qty
                name += it.name + ":" + it.qty.toString() + ","
            }
            name = name.dropLast(1)

        } else {
            val rebuyItemList =
                currentStateRebuyItem?.data?.largeSizeItems?.filter { it.qty > 0 }.orEmpty()

            rebuyItemList.forEach {
                totalqty += it.qty
                name += it.name + ":" + it.qty.toString() + ","
            }
            name = name.dropLast(1)
        }
        return Pair(name, totalqty)
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

    private val _rebuyPriceFromShopLiveData = MutableLiveData<Long>()
    val rebuyPriceFromShopLiveData: LiveData<Long>
        get() = _rebuyPriceFromShopLiveData

    fun setRebuyPriceFromShopOnlyValue(price:Long) {
        _rebuyPriceFromShopLiveData.value = price
    }
    fun setRebuyPriceFromShop(
        wastageYwae: Double,
        maintenanceCost: Int,
        ptClipCost: Int,
        hasGeneralExpense: Boolean
    ) {
        val goldAndGemWeightYwae =
            if (_goldAndGemWeightYwaeLiveData.value != 0.0) _goldAndGemWeightYwaeLiveData.value!! else getYwaeFromGram(
                _goldAndGemWeightGmLiveData.value!!
            )
        val goldKyat =
            (goldAndGemWeightYwae - _gemWeightYwaeLiveData.value!! - _impurityWeightYwaeLiveData.value!!) / 128
        val wastageKyat = wastageYwae / 128
        val diamondGemValue = _gemValueLiveData.value!!

        val otherReducedCosts = diamondGemValue + maintenanceCost + ptClipCost
        val rebuyPriceCurrentState = _rebuyPriceLiveData.value as? Resource.Success<Long>
        val rebuyPrice = rebuyPriceCurrentState?.data ?: 0L
        val buyPriceFromShop = if (hasGeneralExpense) {
            (goldKyat + wastageKyat) * rebuyPrice + otherReducedCosts
        } else {
            goldKyat * rebuyPrice
        }
        _rebuyPriceFromShopLiveData.value = getRoundDownForPrice(buyPriceFromShop.toInt()).toLong()
    }


    private val _rebuyPriceLiveData = SingleLiveEvent<Resource<Long>>()
    val rebuyPriceLiveData: SingleLiveEvent<Resource<Long>>
        get() = _rebuyPriceLiveData

    fun setRebuyPrice(price: Long) {
        _rebuyPriceLiveData.value = Resource.Success(price)
    }

    private val _calculateStateLiveData = MutableLiveData<Boolean>()
    val calculateStaeLiveData: LiveData<Boolean>
        get() = _calculateStateLiveData

    fun getCalculateStateLiveData() {
        val hasGoldAndGemWeight =
            _goldAndGemWeightGmLiveData.value != 0.0 || _goldAndGemWeightYwaeLiveData.value != 0.0
        val hasRebuyPrice = _rebuyPriceLiveData.value!!.data != 0L

        _calculateStateLiveData.value = hasGoldAndGemWeight && hasRebuyPrice
    }

    fun getSaveButtonState():Pair<String,Boolean>{
        val hasPriceA = if (_priceALiveData.value == 0L){
            Pair("ဝယ်စျေး",false)
        }else if (_decidedPawnPriceLiveData.value == 0L){
            Pair("သတ်မှတ်အပေါင်စျေး",false)

        }else if ( _priceCLiveData.value == 0L){
            Pair("ဘောင်ချာဖွင့်ဝယ်စျေး",false)

        }else{
            Pair("Success",true)
        }

        return hasPriceA

    }

    fun resetPrices() {
        _priceBLiveData.value = 0L
        _priceCLiveData.value = 0L
        _dWeightYwaeInVoucherLiveData.value = 0.0
        _decidedPawnPriceLiveData.value = 0
        _calculateStateLiveData.value = false
    }

    fun getRebuyPrice() {
        viewModelScope.launch {
            if (_horizontalOptionLiveData.value.isNullOrEmpty() || _verticalOptionLiveData.value.isNullOrEmpty() || _sizeLiveData.value.isNullOrEmpty()) {

            } else {
                val result = goldFromHomeRepositoryImpl.getRebuyPrice(
                    _horizontalOptionLiveData.value.toString(),
                    _verticalOptionLiveData.value.toString(),
                    _sizeLiveData.value.orEmpty()
                )

                _rebuyPriceLiveData.value = when (result) {
                    is Resource.Loading -> Resource.Loading()
                    is Resource.Error -> Resource.Error(result.message.orEmpty())
                    is Resource.Success -> {
                        (result.data?.price ?: "0").toLong().let { Resource.Success(it) }
                    }
                }
            }
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
            val result = goldFromHomeRepositoryImpl.getPawnDiffValue()
            _pawnDiffValueLiveData.value = when (result) {
                is Resource.Success -> {
                    result.data
                }

                else -> "0"
            }
        }
    }

    fun delaySecond(afterDelay:()->Unit){
        viewModelScope.launch {
            delay(2000)
            afterDelay()
        }
    }

    private val _createStockFromHomeInfoLiveData =
        MutableLiveData<Resource<String>>()
    val createStockFromHomeInfoLiveData: LiveData<Resource<String>>
        get() = _createStockFromHomeInfoLiveData

    fun createStockFromHome(
        imageId: String?,
        itemType:String,
        hasGeneralExpense: String,
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
            val a_buying_price = _priceALiveData.value?.let {
                MultipartBody.Part.createFormData("a_buying_price", it.toString())
            }
            val imageId = imageId?.let { MultipartBody.Part.createFormData("image[id]", it) }
            val b_voucher_buying_value = _priceBLiveData.value?.let {
                MultipartBody.Part.createFormData("b_voucher_buying_value", it.toString())
            }
            val c_voucher_buying_price =
                _priceCLiveData.value?.let {
                MultipartBody.Part.createFormData("c_voucher_buying_price", it.toString())
            }
            val calculated_buying_value =
                _rebuyPriceFromShopLiveData.value?.let {
                MultipartBody.Part.createFormData("calculated_buying_value", it.toString())
            }

            val calculated_for_pawn =
                _calculatedPawnPiceLiveData.value?.let {
                MultipartBody.Part.createFormData("calculated_for_pawn", it.toString())
            }
            val d_gold_weight_ywae =
                _dWeightYwaeInVoucherLiveData.value?.let {
                MultipartBody.Part.createFormData("d_gold_weight_ywae", it.toString())
            }
            val e_price_from_new_voucher =
                _priceELiveData.value?.let {
                MultipartBody.Part.createFormData("e_price_from_new_voucher", it.toString())
            }

            val f_voucher_shown_gold_weight_ywae =
                _fWeightYwaeLiveData.value?.let {
                MultipartBody.Part.createFormData("f_voucher_shown_gold_weight_ywae", it.toString())
            }

            val gem_value =
                _gemValueLiveData.value?.let {
                MultipartBody.Part.createFormData("gem_value", it.toString())
            }

            val gem_weight_ywae =
                _gemWeightYwaeLiveData.value?.let {
                MultipartBody.Part.createFormData("gem_weight_ywae", it.toString())
            }
            val gold_gem_weight_ywae =
                _goldAndGemWeightYwaeLiveData.value?.let {
                MultipartBody.Part.createFormData("gold_gem_weight_ywae", it.toString())
            }
            val gold_weight_ywae =
                _goldWeightYwaeLiveData.value?.let {
                MultipartBody.Part.createFormData("gold_weight_ywae", it.toString())
            }
            val gq_in_carat = _goldCaratLiveData.value?.let {
                MultipartBody.Part.createFormData("gq_in_carat", it.toString())
            }
            val has_general_expenses =_goldCaratLiveData.value?.let {
                MultipartBody.Part.createFormData("gq_in_carat", it.toString())
            }
                MultipartBody.Part.createFormData("has_general_expenses", hasGeneralExpense.toString())
            val image = if (_imagePathLiveData.value != null) {
                MultipartBody.Part.createFormData(
                    "image[file]",
                    File(_imagePathLiveData.value!!).name,
                    compressImage(_imagePathLiveData.value!!)
                )
            } else null
            val impurities_weight_ywae = _impurityWeightYwaeLiveData.value?.let {
                MultipartBody.Part.createFormData("impurities_weight_ywae", it.toString())
            }
            val maintenance_cost = _maintainenceCostLiveData.value?.let {
                MultipartBody.Part.createFormData("maintenance_cost", it.toString())
            }
            val price_for_pawn = _decidedPawnPriceLiveData.value?.let {
                MultipartBody.Part.createFormData("price_for_pawn", it.toString())
            }
            val pt_and_clip_cost = _ptClipCostLiveData.value?.let {
                MultipartBody.Part.createFormData("pt_and_clip_cost", it.toString())
            }
            val qty =  _totalQtyLiveData.value?.let {
                MultipartBody.Part.createFormData("qty", it.toString())
            }
            val rebuy_price =  _rebuyPriceLiveData.value?.data?.let {
                MultipartBody.Part.createFormData("rebuy_price", it.toString())
            }
            val size =  _sizeLiveData.value?.let {
                MultipartBody.Part.createFormData("size", it.toString())
            }
            val stock_condition = _horizontalOptionLiveData.value?.let {
                MultipartBody.Part.createFormData("stock_condition", it.toString())
            }
            val stock_name = _nameTagLiveData.value?.let {
                MultipartBody.Part.createFormData("stock_name", it.toString())
            }
            val type =
                MultipartBody.Part.createFormData("type", itemType)
            val wastage_ywae =  _wastageYwaeLiveData.value?.let {
                MultipartBody.Part.createFormData("wastage_ywae", it.toString())
            }
            val rebuy_price_vertical_option =
                _verticalOptionLiveData.value?.let {
                    MultipartBody.Part.createFormData("rebuy_price_vertical_option", it.toString())
                }

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
        val priceE =
            if (localDatabase.getEValue().isNullOrEmpty()) "0" else localDatabase.getEValue()
        setpriceE((priceE ?: "0").toLong())
    }

    private val _updateStockFromHomeInfoLiveData =
        MutableLiveData<Resource<String>>()
    val updateStockFromHomeInfoLiveData: LiveData<Resource<String>>
        get() = _updateStockFromHomeInfoLiveData

    fun updateStockFromHome(
        imageId: String?,
        oldImageUrl: String?,
        itemType:String,
        hasGeneralExpense: String,
        productIdList: List<String>?,
        isEditable: Boolean,
        isChecked: Boolean,
        isPawn: Boolean,
        oldStockId:String

    ) {
        viewModelScope.launch {
            val sessionKey = if (isPawn) {
                localDatabase.getPawnOldStockSessionKey()
            } else {
                localDatabase.getStockFromHomeSessionKey()
            }
            val id =  MultipartBody.Part.createFormData("id", oldStockId)

            val a_buying_price = _priceALiveData.value?.let {
                MultipartBody.Part.createFormData("a_buying_price", it.toString())
            }
            val b_voucher_buying_value = _priceBLiveData.value?.let {
                MultipartBody.Part.createFormData("b_voucher_buying_value", it.toString())
            }
            val c_voucher_buying_price =
                _priceCLiveData.value?.let {
                    MultipartBody.Part.createFormData("c_voucher_buying_price", it.toString())
                }
            val calculated_buying_value =
                _rebuyPriceFromShopLiveData.value?.let {
                    MultipartBody.Part.createFormData("calculated_buying_value", it.toString())
                }

            val calculated_for_pawn =
                _calculatedPawnPiceLiveData.value?.let {
                    MultipartBody.Part.createFormData("calculated_for_pawn", it.toString())
                }
            val d_gold_weight_ywae =
                _dWeightYwaeInVoucherLiveData.value?.let {
                    MultipartBody.Part.createFormData("d_gold_weight_ywae", it.toString())
                }
            val e_price_from_new_voucher =
                _priceELiveData.value?.let {
                    MultipartBody.Part.createFormData("e_price_from_new_voucher", it.toString())
                }

            val f_voucher_shown_gold_weight_ywae =
                _fWeightYwaeLiveData.value?.let {
                    MultipartBody.Part.createFormData("f_voucher_shown_gold_weight_ywae", it.toString())
                }

            val gem_value =
                _gemValueLiveData.value?.let {
                    MultipartBody.Part.createFormData("gem_value", it.toString())
                }

            val gem_weight_ywae =
                _gemWeightYwaeLiveData.value?.let {
                    MultipartBody.Part.createFormData("gem_weight_ywae", it.toString())
                }
            val gold_gem_weight_ywae =
                _goldAndGemWeightYwaeLiveData.value?.let {
                    MultipartBody.Part.createFormData("gold_gem_weight_ywae", it.toString())
                }
            val gold_weight_ywae =
                _goldWeightYwaeLiveData.value?.let {
                    MultipartBody.Part.createFormData("gold_weight_ywae", it.toString())
                }
            val gq_in_carat = _goldCaratLiveData.value?.let {
                MultipartBody.Part.createFormData("gq_in_carat", it.toString())
            }
            val has_general_expenses =_goldCaratLiveData.value?.let {
                MultipartBody.Part.createFormData("gq_in_carat", it.toString())
            }
            MultipartBody.Part.createFormData("has_general_expenses", hasGeneralExpense.toString())
            val imageId =imageId?.let {MultipartBody.Part.createFormData("image[id]", it)  }
            val image = if (_imagePathLiveData.value != oldImageUrl) {
                MultipartBody.Part.createFormData(
                    "image[file]",
                    File(_imagePathLiveData.value!!).name,
                    compressImage(_imagePathLiveData.value!!)
                )
            } else null
            val impurities_weight_ywae = _impurityWeightYwaeLiveData.value?.let {
                MultipartBody.Part.createFormData("impurities_weight_ywae", it.toString())
            }
            val maintenance_cost = _maintainenceCostLiveData.value?.let {
                MultipartBody.Part.createFormData("maintenance_cost", it.toString())
            }
            val price_for_pawn = _decidedPawnPriceLiveData.value?.let {
                MultipartBody.Part.createFormData("price_for_pawn", it.toString())
            }
            val pt_and_clip_cost = _ptClipCostLiveData.value?.let {
                MultipartBody.Part.createFormData("pt_and_clip_cost", it.toString())
            }
            val qty =  _totalQtyLiveData.value?.let {
                MultipartBody.Part.createFormData("qty", it.toString())
            }
            val rebuy_price =  _rebuyPriceLiveData.value?.data?.let {
                MultipartBody.Part.createFormData("rebuy_price", it.toString())
            }
            val size =  _sizeLiveData.value?.let {
                MultipartBody.Part.createFormData("size", it.toString())
            }
            val stock_condition = _horizontalOptionLiveData.value?.let {
                MultipartBody.Part.createFormData("stock_condition", it.toString())
            }
            val stock_name = _nameTagLiveData.value?.let {
                MultipartBody.Part.createFormData("stock_name", it.toString())
            }
            val type =
                MultipartBody.Part.createFormData("type", itemType)
            val wastage_ywae =  _wastageYwaeLiveData.value?.let {
                MultipartBody.Part.createFormData("wastage_ywae", it.toString())
            }
            val rebuy_price_vertical_option =
                _verticalOptionLiveData.value?.let {
                    MultipartBody.Part.createFormData("rebuy_price_vertical_option", it.toString())
                }

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
        if (!isTheSameQty) {
            _getGemWeightDetailLiveData.value =
                Resource.Success(changeGemQty(id, qty, currentState?.data.orEmpty()))
        }
    }

    fun changeGemQty(
        id: String,
        qty: Int,
        list: List<GemWeightDetailDomain>
    ): List<GemWeightDetailDomain> {
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
        val isTheSameWeightGm =
            currentState?.data?.find { it.id == id }?.gem_weight_gm_per_unit == weightGm
        if (!isTheSameWeightGm) {
            _getGemWeightDetailLiveData.value =
                Resource.Success(changeGemWeightGm(id, weightGm, currentState?.data.orEmpty()))
        }
    }

    fun changeGemWeightGm(
        id: String,
        weightGm: Double,
        list: List<GemWeightDetailDomain>
    ): List<GemWeightDetailDomain> {
        return list.map {
            if (it.id == id) {
                it.copy(gem_weight_gm_per_unit = weightGm)
            } else {
                it
            }
        }
    }

    fun onChangeTotalGemWeight(id: String, qty: Int, gemWeightYwaePerUnit: Double) {
        val currentState =
            _getGemWeightDetailLiveData.value as? Resource.Success<List<GemWeightDetailDomain>>
        val totalGemWeight = qty * gemWeightYwaePerUnit
        val isTheSameTotalGemWeight =
            currentState?.data?.find { it.id == id }?.totalWeightYwae == totalGemWeight
        if (!isTheSameTotalGemWeight) {
            _getGemWeightDetailLiveData.value = Resource.Success(
                changeTotalGemWeight(
                    id,
                    totalGemWeight,
                    currentState?.data.orEmpty()
                )
            )
        }
    }

    fun getTotalCalculatedGemWeightYwae(): Double {
        val currentState =
            _getGemWeightDetailLiveData.value as? Resource.Success<List<GemWeightDetailDomain>>
        var totalWeight = 0.0
        currentState?.data?.forEach {
            totalWeight += it.totalWeightYwae
        }
        return totalWeight
    }

    fun changeTotalGemWeight(
        id: String,
        totalGemWeight: Double,
        list: List<GemWeightDetailDomain>
    ): List<GemWeightDetailDomain> {
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
        if (!isTheSameKpy) {
            _getGemWeightDetailLiveData.value =
                Resource.Success(changeGemWeightDetailKpy(id, ywae, currentState?.data.orEmpty()))
        }
    }

    fun changeGemWeightDetailKpy(
        id: String,
        ywae: Double,
        list: List<GemWeightDetailDomain>
    ): List<GemWeightDetailDomain> {
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
    fun saveGemWeightDetailSessionKey(sessionKey:String?){
        localDatabase.saveGemWeightDetailSessionKey(sessionKey.orEmpty())
    }

    fun getGemWeightDetailSessionKey():String{
      return  localDatabase.getGemWeightDetailSessionKey().orEmpty()
    }

}