package com.example.shwemisale

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
class LoginViewModel @Inject constructor(
    private val authRepoImpl: AuthRepoImpl
): ViewModel(){
    //profile
    private val _loginLiveData = MutableLiveData<Resource<String>?>()
    val loginLiveData: LiveData<Resource<String>?>
        get() = _loginLiveData


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