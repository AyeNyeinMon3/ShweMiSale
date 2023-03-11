package com.example.shwemisale.screen.sellModule.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.sample.SampleDomain
import com.example.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.room_database.entity.asDomain
import com.example.shwemisale.room_database.entity.asEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class OutsideStockViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl
) : ViewModel() {
    var selectedImgUri: File?= null

    private val _outsideSampleLiveData = MutableLiveData<Resource<SampleDomain>>()
    val outsideSampleLiveData: LiveData<Resource<SampleDomain>>
        get() = _outsideSampleLiveData

    fun saveOutside(
        name: RequestBody?,
        weight: RequestBody?,
        specification: RequestBody?,
        image: MultipartBody.Part
    ) {
        _outsideSampleLiveData.value = Resource.Loading()
        viewModelScope.launch {
            _outsideSampleLiveData.value =
                normalSaleRepositoryImpl.saveOutsideSample(name, weight, specification, image)
        }
    }

    var samplesFromRoom =  normalSaleRepositoryImpl.getSamplesFromRoom()


    fun removeSampleFromRoom(sample:SampleDomain){
        viewModelScope.launch {
            appDatabase.sampleDao.deleteSamples(sample.asEntity())
            samplesFromRoom =  normalSaleRepositoryImpl.getSamplesFromRoom()
        }
    }

//    fun getSamplesFromRoom():List<SampleDomain>{
//        return  appDatabase.sampleDao.getSamples().map { it.asDomain() }
//    }
}