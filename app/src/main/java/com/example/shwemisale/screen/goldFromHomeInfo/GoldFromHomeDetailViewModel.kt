package com.example.shwemisale.screen.goldFromHomeInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.StockFromHomeInfoEntity
import com.example.shwemisale.room_database.entity.asUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoldFromHomeDetailViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val appDatabase: AppDatabase
) :ViewModel(){
    var nameTag = ""
    var rebuyItemList = listOf<RebuyItemDto>()
    var totalQty = 0

    var horizontalOption = "Damage"
    var verticalOption = "X"
    var size = "small"

    var hundredPercentGoldPrice = ""
    var pawnDiffValue = "0"

    var gemWeightCustomList = mutableListOf<GemWeightInStockFromHome>()

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

    fun getRebuyPrice(horizontal:String,vertical:String,size:String,id: String?){
        viewModelScope.launch {
            _rebuyPriceLiveData.value = goldFromHomeRepositoryImpl.getRebuyPrice(horizontal,vertical,size)
        }
    }

    private val _rebuyItemeLiveData = MutableLiveData<Resource<List<RebuyItemDto>>>()
    val rebuyItemeLiveData: LiveData<Resource<List<RebuyItemDto>>>
        get() = _rebuyItemeLiveData

    fun getRebuyItem(size:String){
        _rebuyItemeLiveData.value= Resource.Loading()
        viewModelScope.launch {
            _rebuyItemeLiveData.value = goldFromHomeRepositoryImpl.getRebuyItem(size)
        }
    }

    private val _goldTypePriceLiveData = MutableLiveData<Resource<List<GoldTypePriceDto>>>()
    val goldTypePriceLiveData: LiveData<Resource<List<GoldTypePriceDto>>>
        get() = _goldTypePriceLiveData

    fun getGoldTypePrice(){
        _goldTypePriceLiveData.value= Resource.Loading()
        viewModelScope.launch {
            _goldTypePriceLiveData.value = goldFromHomeRepositoryImpl.getGoldType("1")
        }
    }

    fun getPawnDiffValue(){
        viewModelScope.launch {
            pawnDiffValue = goldFromHomeRepositoryImpl.getPawnDiffValue().data?:"0"
        }
    }


    fun getStockInfoFromDataBase(id:String):StockFromHomeInfoUiModel{
        return appDatabase.stockFromHomeInfoDao.getStockFromHomeInfoById(id).asUiModel()
    }

    init {
        getGoldTypePrice()
        getPawnDiffValue()
    }

    fun saveStockFromHome(
        item:StockFromHomeInfoEntity
    ){
        viewModelScope.launch {
            appDatabase.stockFromHomeInfoDao.saveStockFromHomeInfoSingle(
               item
            )
        }
    }
    //val derived_gold_type_id: String?,
    //
    //    val derived_net_gold_weight_kpy: String?,
    //
    //    val derived_net_gold_weight_ywae: String?,
    //
    //    val gem_value: String?,
    //
    //
    //    val gem_weight_ywae: String?,
    //
    //    val gold_and_gem_weight_gm: String?,
    //
    //    val gold_price: String?,
    //    val image: String?,
    //    val maintenance_cost: String?,
    //    val name: String?,
    //    val pt_and_clip_cost: String?,

    fun updateStockFromHome(
        id:String,
        name:String,
        qty:String,
        size:String,
        goldWeightYwae:String,
        derived_net_gold_weight_ywae:String,
        gemValue:String,
        gem_details_qty: List<String>,
        gem_details_gm_per_units: List<String>,
        gem_details_ywae_per_units: List<String>,
        gemWeightYwae:String,
        gold_and_gem_weight_gm:String,
        goldPrice:String,
        maintenance_cost:String,
        pt_and_clip_cost:String,
        rebuyPrice:String,
        reducedCosts:String,
        priceForPawn:String,
        calculatedPriceForPawn: String,
        wastageYwae: String,
        oldStockCondition: String,
        oldStockGQinCarat: String,
        oldStockImpurityWeightY: String,
        oldstockABuyingPrice: String,
        bPrice: String,
        cPrice: String,
        oldStockDGoldWeightY: String,
        ePrice: String,
        oldStockFVoucherShownGoldWeightY: String,
    ){
        viewModelScope.launch {
            appDatabase.stockFromHomeInfoDao.updateName(id,name)
            appDatabase.stockFromHomeInfoDao.updateQty(id,qty)
            appDatabase.stockFromHomeInfoDao.updateSize(id,size)
            appDatabase.stockFromHomeInfoDao.updateGoldWeighYwae(id,goldWeightYwae)
            appDatabase.stockFromHomeInfoDao.updateDerivedNetGoldWeightYwae(id,derived_net_gold_weight_ywae)
            appDatabase.stockFromHomeInfoDao.updateGemValue(id,gemValue)
            appDatabase.stockFromHomeInfoDao.updateGemWeightYwae(id,gemWeightYwae)
            appDatabase.stockFromHomeInfoDao.updateGemDetailsQty(id,gem_details_qty)
            appDatabase.stockFromHomeInfoDao.updateGemDetailsGmPerUnits(id,gem_details_gm_per_units)
            appDatabase.stockFromHomeInfoDao.updateGemDetailsYwaePerUnits(id,gem_details_ywae_per_units)
            appDatabase.stockFromHomeInfoDao.updateGoldAndGemWeightGm(id,gold_and_gem_weight_gm)
            appDatabase.stockFromHomeInfoDao.updateGoldPrice(id,goldPrice)
            appDatabase.stockFromHomeInfoDao.updateMaintenanceCost(id,maintenance_cost)
            appDatabase.stockFromHomeInfoDao.updatePtAndClipCost(id,pt_and_clip_cost)

            appDatabase.stockFromHomeInfoDao.updateRebuyPrice(id,rebuyPrice)
            appDatabase.stockFromHomeInfoDao.updateReducedCost(id,reducedCosts)
            appDatabase.stockFromHomeInfoDao.updatePriceForPawn(id,priceForPawn)
            appDatabase.stockFromHomeInfoDao.updateCalculatedPriceForPawn(id,calculatedPriceForPawn)
            appDatabase.stockFromHomeInfoDao.updateWastageYwae(id,wastageYwae)
            appDatabase.stockFromHomeInfoDao.updateOldStockCondition(id,oldStockCondition)
            appDatabase.stockFromHomeInfoDao.updateOldStockGQinCarat(id,oldStockGQinCarat)
            appDatabase.stockFromHomeInfoDao.updateOldStockImpurityWeightY(id,oldStockImpurityWeightY)
            appDatabase.stockFromHomeInfoDao.updateOldStockABuyingPrice(id,oldstockABuyingPrice)
            appDatabase.stockFromHomeInfoDao.updateBprice(id,bPrice)
            appDatabase.stockFromHomeInfoDao.updateCprice(id,cPrice)
            appDatabase.stockFromHomeInfoDao.updateDprice(id,oldStockDGoldWeightY)
            appDatabase.stockFromHomeInfoDao.updateEprice(id,ePrice)
            appDatabase.stockFromHomeInfoDao.updateFprice(id,oldStockFVoucherShownGoldWeightY)
        }
    }

    var selectedImagePath = ""
    fun saveImage(id:String,path:String){
        viewModelScope.launch {
            appDatabase.stockFromHomeInfoDao.updateImage(id,path)
        }
    }
}