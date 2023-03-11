package com.example.shwemisale.repository

import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeInfoDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockWeightByVoucherDomain
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.StockFromHomeInfoDto

interface GoldFromHomeRepository {
    suspend fun getStockWeightByVoucher(voucherCode:String):Resource<List<StockWeightByVoucherDomain>>
    suspend fun getStockInfoByVoucher(voucherCode:String,productIdList:List<String>):Resource<List<StockFromHomeInfoDomain>>
    suspend fun getRebuyPrice(horizontal_option_name:String,vertical_option_name:String,size:String):Resource<RebuyPriceDto>
    suspend fun getRebuyItem(size:String):Resource<List<RebuyItemDto>>
    suspend fun getGoldType(goldTypeId:String?):Resource<List<GoldTypePriceDto>>
    suspend fun getPawnDiffValue():Resource<String>
}