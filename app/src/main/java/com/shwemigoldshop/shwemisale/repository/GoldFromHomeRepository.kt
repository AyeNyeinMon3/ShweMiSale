package com.shwemigoldshop.shwemisale.repository

import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockWeightByVoucherDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.shwemigoldshop.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import okhttp3.RequestBody

interface GoldFromHomeRepository {
    suspend fun getStockWeightByVoucher(voucherCode:String): Resource<List<StockWeightByVoucherDomain>>
    suspend fun getStockInfoByVoucher(voucherCode:String,productIdList:List<String>,goldPrice18KId:String): Resource<List<StockFromHomeDomain>>
    suspend fun getRebuyPrice(horizontal_option_name:String,vertical_option_name:String,size:String): Resource<RebuyPriceDto>
    suspend fun getRebuyItem(size:String): Resource<List<RebuyItemDto>>
    suspend fun getGoldType(goldTypeId:String?): Resource<List<GoldTypePriceDto>>
    suspend fun getPawnDiffValue(): Resource<String>

    suspend fun createGemWeightDetail(
         qty: RequestBody,
         weightGmPerUnit: RequestBody,
         weightYwaePerUnit: RequestBody,
    ): Resource<String>

    suspend fun updateGemWeightDetail(
        id:RequestBody,
         qty: RequestBody,
         weightGmPerUnit: RequestBody,
         weightYwaePerUnit: RequestBody,
    ): Resource<String>

    suspend fun getGemWeightDetail(sessionKey:String): Resource<List<GemWeightDetailDomain>>

    suspend fun deleteGemWeightDetail(
       id:String
    ): Resource<String>
}