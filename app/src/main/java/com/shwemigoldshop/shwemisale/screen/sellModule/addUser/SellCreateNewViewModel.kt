package com.shwemigoldshop.shwemisale.screen.sellModule.addUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.customers.CustomerDataDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.customers.ProvinceDto
import com.shwemigoldshop.shwemisale.data_layers.dto.customers.TownshipDto
import com.shwemigoldshop.shwemisale.repositoryImpl.CustomerRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellCreateNewViewModel @Inject constructor(
    private val customerRepoImpl: CustomerRepoImpl
) :ViewModel() {
    //addUser
    private val _addUserLiveData = MutableLiveData<Resource<CustomerDataDomain>?>()
    val addUserLiveData: LiveData<Resource<CustomerDataDomain>?>
        get() = _addUserLiveData

    fun resetAddUserLiveData() {
        _addUserLiveData.value = null
    }

    fun addUser(
        name: String,
        phone: String,
        date_of_birth: String?,
        gender: String,
        province_id: String,
        township_id: String,
        address: String,
        nrc: String?
    ) {
        _addUserLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _addUserLiveData.value = customerRepoImpl.addNewUser(
                name, phone, date_of_birth, gender, province_id, township_id, address, nrc
            )
        }
    }

    //province
    private val _provinceLiveData = MutableLiveData<Resource<List<ProvinceDto>>?>()
    val provinceLiveData: LiveData<Resource<List<ProvinceDto>>?>
        get() = _provinceLiveData

    fun resetProvinceLiveData() {
        _provinceLiveData.value = null
    }

    fun getProvince() {
        _provinceLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _provinceLiveData.value = customerRepoImpl.getProvince()
        }
    }


    private val _townShipLiveData = MutableLiveData<Resource<List<TownshipDto>>?>()
    val townShipLiveData: LiveData<Resource<List<TownshipDto>>?>
        get() = _townShipLiveData

    fun resetTownshipLiveData() {
        _townShipLiveData.value = null
    }

    fun getTownShip(province_id: String) {
        _townShipLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _townShipLiveData.value = customerRepoImpl.getTownship(province_id)
        }
    }

    init {
        getProvince()
    }

}