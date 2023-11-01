package com.shwemigoldshop.shwemisale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.repositoryImpl.AuthRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepoImpl: AuthRepoImpl,
    private val localDatabase: LocalDatabase
): ViewModel(){
    //profile
    private val _logoutLiveData = MutableLiveData<Resource<String>?>()
    val logoutLiveData: LiveData<Resource<String>?>
        get() = _logoutLiveData




    fun resetLoginLiveData(){
        _logoutLiveData.value = null
    }
    fun logout(){
        _logoutLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _logoutLiveData.value = authRepoImpl.logout()
        }
    }
}