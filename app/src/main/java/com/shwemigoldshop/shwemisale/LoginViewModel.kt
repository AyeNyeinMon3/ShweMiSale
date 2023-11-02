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
class LoginViewModel @Inject constructor(
    private val authRepoImpl: AuthRepoImpl,
    private val localDatabase: LocalDatabase
): ViewModel(){
    //profile
    private val _loginLiveData = MutableLiveData<Resource<String>?>()
    val loginLiveData: LiveData<Resource<String>?>
        get() = _loginLiveData

    init {
        localDatabase.clearSharedPreference()
    }

    fun resetLoginLiveData(){
        _loginLiveData.value = null
    }
    fun login(userName:String,password:String){
        _loginLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _loginLiveData.value = authRepoImpl.login(userName,password)
        }
    }

}