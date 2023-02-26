package com.example.shwemisale.screen.sellModule.exchangeOrderAndOldItem

import androidx.lifecycle.ViewModel
import com.example.shwemisale.localDataBase.LocalDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExchangeOrderViewModel @Inject constructor(
    private val localDatabase: LocalDatabase
):ViewModel(){
}