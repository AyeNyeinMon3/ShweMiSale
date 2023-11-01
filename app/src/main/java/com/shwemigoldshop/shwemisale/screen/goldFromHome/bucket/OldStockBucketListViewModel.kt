package com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket

import androidx.lifecycle.ViewModel
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.repositoryImpl.GoldFromHomeRepositoryImpl
import com.shwemigoldshop.shwemisale.repositoryImpl.NormalSaleRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OldStockBucketListViewModel @Inject constructor(
    private val goldFromHomeRepositoryImpl: GoldFromHomeRepositoryImpl,
    private val normalSaleRepositoryImpl: NormalSaleRepositoryImpl,
    private val localDatabase: LocalDatabase
):ViewModel() {


}