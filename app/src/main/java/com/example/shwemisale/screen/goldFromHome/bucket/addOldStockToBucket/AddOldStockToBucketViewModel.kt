package com.example.shwemisale.screen.goldFromHome.bucket.addOldStockToBucket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddOldStockToBucketViewModel @Inject constructor():ViewModel() {
    private val _imagePathLiveData = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String>
        get() = _imagePathLiveData

    fun setImagePathLiveData(imagePath: String) {
        _imagePathLiveData.value = imagePath
    }}