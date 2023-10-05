package com.example.shwemisale.screen.goldFromHome.bucket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.ui_models.OldStockBucketUiModel
import dagger.hilt.android.lifecycle.HiltViewModel

class BucketShareViewModel:ViewModel() {
    var list = mutableListOf<OldStockBucketUiModel>()
    val oldStockInBucketList = MutableLiveData<MutableList<OldStockBucketUiModel>>()
    val dataFilledOldStock = mutableListOf<StockFromHomeDomain>()

    fun addToOldStockBucket(item:OldStockBucketUiModel){
        list.add(item)
        oldStockInBucketList.value = list
    }
    fun removeOldStockBucket(item:OldStockBucketUiModel){
        list.remove(item)
        oldStockInBucketList.value = list
    }
}

