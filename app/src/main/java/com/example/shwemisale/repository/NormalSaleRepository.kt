package com.example.shwemisale.repository

import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.generalSale.GeneralSaleListDomain
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.domain.pureGoldSale.PureGoldListDomain
import com.example.shwemisale.data_layers.domain.sample.SampleDomain
import com.example.shwemisale.data_layers.dto.GeneralSaleDto
import com.example.shwemisale.data_layers.dto.calculation.GoldPriceDto
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.data_layers.dto.goldFromHome.Image
import com.example.shwemisale.data_layers.dto.sample.SampleDto
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithKPYDto
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithValueResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface NormalSaleRepository {
    suspend fun getPaidAmountOfVoucher(
        voucherCode: String
    ): Resource<String>

    suspend fun getGoldPrices(
        productIdList: List<String>
    ): Resource<GoldPriceDto>

    suspend fun getVoucherInfoWithKPY(
        productIdList: List<String>
    ): Resource<VoucherInfoWithKPYDto>

    suspend fun getVoucherInfoWithValue(
        productIdList: List<String>
    ): Resource<VoucherInfoWithValueResponse>

    suspend fun getStockFromHomeList(
       sessionKey: String?
    ):Resource<List<StockFromHomeDomain>>

    suspend fun createStockFromHomeList(
        a_buying_price: String?,
         b_voucher_buying_value: String?,
         c_voucher_buying_price: String?,
         calculated_buying_value: String?,
         calculated_for_pawn: String?,
         d_gold_weight_ywae: String?,
         e_price_from_new_voucher: String?,
         f_voucher_shown_gold_weight_ywae: String?,
         gem_value: String?,
        gem_weight_details_qty: List<MultipartBody.Part?>?,
        gem_weight_details_gm: List<MultipartBody.Part?>?,
        gem_weight_details_ywae: List<MultipartBody.Part?>?,
         gem_weight_ywae: String?,
         gold_gem_weight_ywae: String?,
         gold_weight_ywae: String?,
         gq_in_carat: String?,
         has_general_expenses: String?,
       imageId: String?,
     imageFile: RequestBody?,
         impurities_weight_ywae: String?,
         maintenance_cost: String?,
         price_for_pawn: String?,
         pt_and_clip_cost: String?,
         qty: String?,
         rebuy_price: String?,
         size: String?,
         stock_condition: String?,
         stock_name: String?,
         type: String?,
         wastage_ywae: String?,
        rebuy_price_vertical_option: String?,
        sessionKey: String?
    ):Resource<String>

    suspend fun updateStockFromHomeList(
        a_buying_price: List<MultipartBody.Part>?,
        b_voucher_buying_value: List<MultipartBody.Part>?,
        c_voucher_buying_price: List<MultipartBody.Part>?,
        calculated_buying_value: List<MultipartBody.Part>?,
        calculated_for_pawn: List<MultipartBody.Part>?,
        d_gold_weight_ywae: List<MultipartBody.Part>?,
        e_price_from_new_voucher: List<MultipartBody.Part>?,
        f_voucher_shown_gold_weight_ywae: List<MultipartBody.Part>?,
        gem_value: List<MultipartBody.Part>?,
        gem_weight_details_qty: List<MultipartBody.Part?>?,
        gem_weight_details_gm: List<MultipartBody.Part?>?,
        gem_weight_details_ywae: List<MultipartBody.Part?>?,
        gem_weight_ywae: List<MultipartBody.Part>?,
        gold_gem_weight_ywae: List<MultipartBody.Part>?,
        gold_weight_ywae: List<MultipartBody.Part>?,
        gq_in_carat: List<MultipartBody.Part>?,
        has_general_expenses: List<MultipartBody.Part>?,
        imageId: List<MultipartBody.Part>?,
        imageFile: List<MultipartBody.Part>?,
        impurities_weight_ywae: List<MultipartBody.Part>?,
        maintenance_cost: List<MultipartBody.Part>?,
        price_for_pawn: List<MultipartBody.Part>?,
        pt_and_clip_cost: List<MultipartBody.Part>?,
        qty: List<MultipartBody.Part>?,
        rebuy_price: List<MultipartBody.Part>?,
        size: List<MultipartBody.Part>?,
        stock_condition: List<MultipartBody.Part>?,
        stock_name: List<MultipartBody.Part>?,
        type: List<MultipartBody.Part>?,
        wastage_ywae: List<MultipartBody.Part>?,
        rebuy_price_vertical_option:  List<MultipartBody.Part>?,
        sessionKey: String?
    ):Resource<String>

    suspend fun submitWithKPY(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_stock_session_key: RequestBody,

        ): Resource<String>


    suspend fun submitWithValue(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_stock_session_key: RequestBody,

        ): Resource<String>


    /** New Order Sales */

    suspend fun submitOrderSale(
        name: String,
        gold_type_id: String,
        gold_price: String,
        total_gold_weight_ywae: String,
        est_unit_wastage_ywae: String,
        qty: String,
        gem_value: String,
        maintenance_cost: String,
        date_of_delivery: String,
        remark: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stock_session_key: RequestBody,
        oldStockSampleListId: List<MultipartBody.Part>?
    ): Resource<String>


    suspend fun submitPureGoldSale(
        sessionKey:String,
        gold_price: String,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stock_session_key: RequestBody,


    ): Resource<String>


    suspend fun submitGeneralSale(
        itemsGeneralSaleItemId: List<MultipartBody.Part>?,
        itemsQty: List<MultipartBody.Part>?,
        itemsGoldWeightYwae: List<MultipartBody.Part>?,
        itemsWastageYwae: List<MultipartBody.Part>?,
        itemsMaintenanceCost: List<MultipartBody.Part>?,
        user_id: String,
        paid_amount: String,
        reduced_cost: String,
        old_stock_session_key: RequestBody,


        ): Resource<String>


    suspend fun getGeneralSalesItems(
    ): Resource<List<GeneralSaleDto>>

    suspend fun checkSample(productId: String): Resource<SampleDomain>
    suspend fun saveSample(samples: HashMap<String, String>): Resource<SampleDomain>
    suspend fun saveOutsideSample(
        name: RequestBody?,
        weight: RequestBody?,
        specification: RequestBody?,
        image: MultipartBody.Part
    ): Resource<SampleDomain>

    suspend fun getPureGoldItems(
        sessionKey: String
    ):Resource<List<PureGoldListDomain>>

    suspend fun updatePureGoldItems(
         gold_weight_ywae: List<MultipartBody.Part>?,
         maintenance_cost: List<MultipartBody.Part>?,
         threading_fees: List<MultipartBody.Part>?,
         type: List<MultipartBody.Part>?,
         wastage_ywae: List<MultipartBody.Part>?,
         sessionKey: String?
    ):Resource<String>

    suspend fun createPureGoldItems(
        gold_weight_ywae: String,
        maintenance_cost: String,
        threading_fees: String,
        type: String,
        wastage_ywae: String,
        sessionKey: String?
    ):Resource<String>

    suspend fun getGeneralSaleItems(
        sessionKey: String
    ):Resource<List<GeneralSaleListDomain>>

    suspend fun updateGeneralSaleItems(
         general_sale_item_id:  List<MultipartBody.Part>?,
         gold_weight_gm:  List<MultipartBody.Part>?,
         maintenance_cost:  List<MultipartBody.Part>?,
         qty:  List<MultipartBody.Part>?,
         wastage_ywae:  List<MultipartBody.Part>?,
         sessionKey: String?
    ):Resource<String>

    suspend fun createGeneralSaleItems(
        general_sale_item_id: String,
        gold_weight_gm: String,
        maintenance_cost: String,
        qty: String,
        wastage_ywae: String,
    ):Resource<String>
}