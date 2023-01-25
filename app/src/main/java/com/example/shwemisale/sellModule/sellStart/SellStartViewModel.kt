package com.example.shwemisale.sellModule.sellStart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.repositoryImpl.AuthRepoImpl
import com.example.shwemisale.repositoryImpl.CustomerRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellStartViewModel @Inject constructor(
    private val customerRepoImpl: CustomerRepoImpl,
    private val authRepoImpl: AuthRepoImpl
):ViewModel(){
    //profile
    private val _profileLiveData = MutableLiveData<Resource<String>?>()
    val profileLiveData: LiveData<Resource<String>?>
        get() = _profileLiveData


    fun resetProfileLiveData(){
        _profileLiveData.value = null
    }
    fun getProfile(){
        _profileLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _profileLiveData.value = authRepoImpl.getProfile()
        }
    }
}