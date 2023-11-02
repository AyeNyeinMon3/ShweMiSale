package com.shwemigoldshop.shwemisale.screen.sellModule.sellStart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.customers.CustomerDataDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.customers.ProvinceDto
import com.shwemigoldshop.shwemisale.data_layers.dto.customers.TownshipDto
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.pagingSource.customer.CustomerSearchPagingDataSource
import com.shwemigoldshop.shwemisale.repositoryImpl.AuthRepoImpl
import com.shwemigoldshop.shwemisale.repositoryImpl.CustomerRepoImpl
import com.shwemigoldshop.shwemisale.room_database.AppDatabase
import com.shwemigoldshop.shwemisale.room_database.entity.StockFromHomeFinalInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellStartViewModel @Inject constructor(
    private val customerRepoImpl: CustomerRepoImpl,
    private val authRepoImpl: AuthRepoImpl,
    private val appDatabase: AppDatabase,
    private val localDatabase: LocalDatabase
) : ViewModel() {
    //profile
    private val _profileLiveData = MutableLiveData<Resource<String>?>()
    val profileLiveData: LiveData<Resource<String>?>
        get() = _profileLiveData

    fun resetProfileLiveData() {
        _profileLiveData.value = null
    }

    fun getProfile() {
        _profileLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _profileLiveData.value = authRepoImpl.getProfile()
        }
    }

    fun saveStockFromHomeInfoFinal() {
        viewModelScope.launch {
            appDatabase.stockFromHomeFinalInfoDao.saveStockFromHomeFinalInfo(
                StockFromHomeFinalInfo(1L, "0", "0", "0")
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

//    private val _customerSearchLiveData = MutableLiveData<Resource<List<CustomerDataUiModel>>?>()
//    val customerSearchLiveData: LiveData<Resource<List<CustomerDataUiModel>>?>
//        get() = _customerSearchLiveData
//
//    fun resetCustomerSearchLiveData() {
//        _customerSearchLiveData.value = null
//    }
//
//    fun searchCustomerData(
//        code: String?,
//        name: String?,
//        phone: String?,
//        date_of_birth: String?,
//        gender: String?,
//        province_id: String?,
//        township_id: String?,
//        address: String?,
//        nrc: String?
//    ) {
//        _customerSearchLiveData.value = Resource.Loading()
//        viewModelScope.launch {
//            val result = customerRepoImpl.getCustomerDataByCode(
//                code,
//                name,
//                phone,
//                date_of_birth,
//                gender,
//                province_id,
//                township_id,
//                address,
//                nrc
//            )
//            when (result) {
//                is Resource.Success -> {
//                    _customerSearchLiveData.value =
//                        Resource.Success(result.data!!.map { it.asUiModel() })
//                }
//                is Resource.Error -> {
//                    _customerSearchLiveData.value = Resource.Error(result.message)
//                }
//                else -> {}
//            }
//        }
//    }

    fun getCustomerInfo(
        code: String?,
        name: String?,
        phone: String?,
        date_of_birth: String?,
        gender: String?,
        province_id: String?,
        township_id: String?,
        address: String?,
        nrc: String?
    ): LiveData<PagingData<CustomerDataDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
            ),
            initialKey = 1,
            pagingSourceFactory = {
                CustomerSearchPagingDataSource(
                    code,
                    name,
                    phone,
                    date_of_birth,
                    gender,
                    province_id,
                    township_id,
                    address,
                    nrc,
                    customerRepoImpl
                )
            }
        ).liveData
    }

    private val _deviceIdLogInLiveData = MutableLiveData<String>()
    val deviceIdLogInLiveData : LiveData<String>
        get() = _deviceIdLogInLiveData
    fun getDeviceIdFromSharedPreference(){
        _deviceIdLogInLiveData.value = localDatabase.getDeviceIdFromServer()
    }
    fun saveDeviceIdFromServer(deviceId:String){
        localDatabase.saveDeviceIdFromServer(deviceId)
        _deviceIdLogInLiveData.value = deviceId
    }

    private val _authorizeAppLiveData = MutableLiveData<Resource<String>?>()
    val authorizeAppLiveData: LiveData<Resource<String>?>
        get() = _authorizeAppLiveData

    fun authorizeApp(){
        _authorizeAppLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _authorizeAppLiveData.value = authRepoImpl.authorizeApp()
        }
    }
}