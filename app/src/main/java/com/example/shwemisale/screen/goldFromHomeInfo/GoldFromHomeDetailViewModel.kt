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
            _goldTypePriceLiveData.value = goldFromHomeRepositoryImpl.getGoldType()
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

    fun updateStockFromHome(
        id:String,
        rebuyPrice:String,
        reducedCosts:String,
        priceForPawn:String,
    ){
        viewModelScope.launch {
            appDatabase.stockFromHomeInfoDao.updateRebuyPrice(id,rebuyPrice)
            appDatabase.stockFromHomeInfoDao.updateReducedCost(id,reducedCosts)
            appDatabase.stockFromHomeInfoDao.updatePriceForPawn(id,reducedCosts)

        }
    }
}