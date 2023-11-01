package com.shwemigoldshop.shwemisale.screen.sellModule.normalSale.customerInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.customers.CustomerWhistListDomain
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.repositoryImpl.CustomerRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellCustomerInfoViewModel @Inject constructor(
    private val customerRepoImpl: CustomerRepoImpl,
    private val localDatabase: LocalDatabase
) :ViewModel(){
    //addUser
    private val _customerWhistListLiveData = MutableLiveData<Resource<List<CustomerWhistListDomain>>?>()
    val customerWhistListLiveData: LiveData<Resource<List<CustomerWhistListDomain>>?>
        get() = _customerWhistListLiveData

    fun getCustomerWhistList(customerId:String){
        _customerWhistListLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _customerWhistListLiveData.value = customerRepoImpl.getCustomerWhistList(customerId)
        }

    }

    fun saveCustomerId(id:String){
        localDatabase.saveCustomerId(id)
    }

}