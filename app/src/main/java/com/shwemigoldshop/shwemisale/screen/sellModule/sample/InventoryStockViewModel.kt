package com.shwemigoldshop.shwemisale.screen.sellModule.sample

import androidx.lifecycle.*
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.sample.SampleDomain
import com.shwemigoldshop.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.shwemigoldshop.shwemisale.repositoryImpl.ProductRepoImpl
import com.shwemigoldshop.shwemisale.room_database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryStockViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val productRepoImpl: ProductRepoImpl
): ViewModel() {
    private val _inventorySampleLiveData = MutableLiveData<Resource<SampleDomain>>()
    val inventorySampleLiveData: LiveData<Resource<SampleDomain>>
        get() = _inventorySampleLiveData

    private val _productIdLiveData = MutableLiveData<Resource<String>>()
    val productIdLiveData: LiveData<Resource<String>>
        get() = _productIdLiveData

    private val _saveSampleLiveData = MutableLiveData<Resource<SampleDomain>>()
    val saveSampleLiveData: LiveData<Resource<SampleDomain>>
        get() = _saveSampleLiveData

    var samplesFromRoom =  normalSaleRepositoryImpl.getSamplesFromRoom()

    fun getProductId(productCode: String) {
        viewModelScope.launch {
            _productIdLiveData.value = Resource.Loading()
            _productIdLiveData.value = productRepoImpl.exChangeProductCodeWithId(productCode)
        }
    }

    fun checkSample(productCode: String) {
        viewModelScope.launch {
            _inventorySampleLiveData.value = Resource.Loading()
            _inventorySampleLiveData.value = normalSaleRepositoryImpl.checkSample(productCode)
        }
    }

    fun removeSampleFromRoom(sample:SampleDomain){
        viewModelScope.launch {
            var rowsDeleted = appDatabase.sampleDao.deleteSamples(sample.localId.orEmpty())
            if (rowsDeleted>0){
                samplesFromRoom =  normalSaleRepositoryImpl.getSamplesFromRoom()
            }
        }
    }

    fun takeSample(samples:List<SampleDomain>) {
        _saveSampleLiveData.value = Resource.Loading()
        val sampleIdHashMap: HashMap<String, String> = HashMap()
        if (samples.map { it.specification }.filter { it.orEmpty().isNotEmpty() }.isEmpty()) {
            _saveSampleLiveData.value = Resource.Error("Please Enter Specification")
        } else {
            repeat(samples.filter {it.id == null}.size) {
                val productId = samples[it].product_id
                val specification = samples[it].specification
                sampleIdHashMap["sample[$productId]"] =
                    specification.orEmpty()
            }
            viewModelScope.launch {
                _saveSampleLiveData.value =  normalSaleRepositoryImpl.saveSample(
                    sampleIdHashMap
                )
            }
        }
    }

    private val _saleSampleLiveData = MutableLiveData<Resource<SampleDomain>>()
    val saleSampleLiveData: LiveData<Resource<SampleDomain>>
        get() = _saleSampleLiveData
}