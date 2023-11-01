package com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket.addOldStockToBucket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddOldStockToBucketViewModel @Inject constructor(
    private val localDatabase: LocalDatabase
) : ViewModel() {
    private val _imagePathLiveData = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String>
        get() = _imagePathLiveData

    fun setImagePathLiveData(imagePath: String) {
        _imagePathLiveData.value = imagePath
    }

    fun getEValue():String{
       return if (localDatabase.getEValue().isNullOrEmpty()) "0" else localDatabase.getEValue().orEmpty()
    }
}