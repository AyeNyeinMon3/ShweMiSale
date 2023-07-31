package com.example.shwemisale.repository

import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockWeightByVoucherDomain
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.calculation.GoldTypePriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.data_layers.dto.goldFromHome.RebuyPriceDto
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface GoldFromHomeRepository {
    suspend fun getStockWeightByVoucher(voucherCode:String):Resource<List<StockWeightByVoucherDomain>>
    suspend fun getStockInfoByVoucher(voucherCode:String,productIdList:List<String>,goldPrice18KId:String):Resource<List<StockFromHomeDomain>>
    suspend fun getRebuyPrice(horizontal_option_name:String,vertical_option_name:String,size:String):Resource<RebuyPriceDto>
    suspend fun getRebuyItem(size:String):Resource<List<RebuyItemDto>>
    suspend fun getGoldType(goldTypeId:String?):Resource<List<GoldTypePriceDto>>
    suspend fun getPawnDiffValue():Resource<String>

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