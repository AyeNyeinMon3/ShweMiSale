package com.shwemigoldshop.shwemisale.repository

import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.data_layers.domain.generalSale.GeneralSaleListDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.PaidAmountOfVoucherDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.pureGoldSale.PureGoldListDomain
import com.shwemigoldshop.shwemisale.data_layers.domain.sample.SampleDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.GeneralSaleDto
import com.shwemigoldshop.shwemisale.data_layers.dto.calculation.GoldPriceDto
import com.shwemigoldshop.shwemisale.data_layers.dto.voucher.VoucherInfoWithKPYDto
import com.shwemigoldshop.shwemisale.data_layers.dto.voucher.VoucherInfoWithValueResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface NormalSaleRepository {
    suspend fun getPaidAmountOfVoucher(
        voucherCode: String
    ): Resource<PaidAmountOfVoucherDomain>

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
       sessionKey: String?,
    ): Resource<List<StockFromHomeDomain>>

    suspend fun getStockFromHomeForPawn(
       pawnVoucherCode: String?,
       is_pawned_sale: String?
    ): Resource<List<StockFromHomeDomain>>

    suspend fun createStockFromHomeList(
        a_buying_price: MultipartBody.Part?,
        b_voucher_buying_value: MultipartBody.Part?,
        c_voucher_buying_price: MultipartBody.Part?,
        calculated_buying_value: MultipartBody.Part?,
        calculated_for_pawn: MultipartBody.Part?,
        d_gold_weight_ywae: MultipartBody.Part?,
        e_price_from_new_voucher: MultipartBody.Part?,
        f_voucher_shown_gold_weight_ywae: MultipartBody.Part?,
        gem_value: MultipartBody.Part?,
        gem_weight_details_session_key : MultipartBody.Part?,
        gem_weight_ywae: MultipartBody.Part?,
        gold_gem_weight_ywae: MultipartBody.Part?,
        gold_weight_ywae: MultipartBody.Part?,
        gq_in_carat: MultipartBody.Part?,
        has_general_expenses: MultipartBody.Part?,
        imageFile: MultipartBody.Part?,
        imageId: MultipartBody.Part?,
        impurities_weight_ywae: MultipartBody.Part?,
        maintenance_cost: MultipartBody.Part?,
        price_for_pawn: MultipartBody.Part?,
        pt_and_clip_cost: MultipartBody.Part?,
        qty: MultipartBody.Part?,
        rebuy_price: MultipartBody.Part?,
        size: MultipartBody.Part?,
        stock_condition: MultipartBody.Part?,
        stock_name: MultipartBody.Part?,
        type: MultipartBody.Part?,
        wastage_ywae: MultipartBody.Part?,
        rebuy_price_vertical_option: MultipartBody.Part?,
        productIdList: List<MultipartBody.Part?>?,
        isEditable:MultipartBody.Part?,
        isChecked:MultipartBody.Part?,
       sessionKey:String?,
        isPawn:Boolean
    ): Resource<String>

    suspend fun updateStockFromHomeList(
        id: MultipartBody.Part?,
        a_buying_price: MultipartBody.Part?,
        b_voucher_buying_value: MultipartBody.Part?,
        c_voucher_buying_price: MultipartBody.Part?,
        calculated_buying_value: MultipartBody.Part?,
        calculated_for_pawn: MultipartBody.Part?,
        d_gold_weight_ywae: MultipartBody.Part?,
        e_price_from_new_voucher: MultipartBody.Part?,
        f_voucher_shown_gold_weight_ywae: MultipartBody.Part?,
        gem_value: MultipartBody.Part?,
        gem_weight_details_session_key : MultipartBody.Part?,
        gem_weight_ywae: MultipartBody.Part?,
        gold_gem_weight_ywae: MultipartBody.Part?,
        gold_weight_ywae: MultipartBody.Part?,
        gq_in_carat: MultipartBody.Part?,
        has_general_expenses: MultipartBody.Part?,
        imageFile: MultipartBody.Part?,
        impurities_weight_ywae: MultipartBody.Part?,
        maintenance_cost: MultipartBody.Part?,
        price_for_pawn: MultipartBody.Part?,
        pt_and_clip_cost: MultipartBody.Part?,
        qty: MultipartBody.Part?,
        rebuy_price: MultipartBody.Part?,
        size: MultipartBody.Part?,
        stock_condition: MultipartBody.Part?,
        stock_name: MultipartBody.Part?,
        type: MultipartBody.Part?,
        wastage_ywae: MultipartBody.Part?,
        rebuy_price_vertical_option: MultipartBody.Part?,
        productIdList: List<MultipartBody.Part?>?,
        isEditable:MultipartBody.Part?,
        isChecked:MultipartBody.Part?,
        sessionKey:String?,
        isPawn:Boolean
    ): Resource<String>

    suspend fun updateEValue(
        eValue:String,
        sessionKey: String?
    ): Resource<String>

    suspend fun deleteOldStock(
        oldStockId:String
    ): Resource<String>

    suspend fun submitWithKPY(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,
        redeem_point: RequestBody?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_voucher_code:RequestBody?,
        old_stock_session_key: RequestBody?,
        old_stock_calc_type: RequestBody,

        ): Resource<String>


    suspend fun submitWithValue(
        productIdList: List<MultipartBody.Part>?,
        user_id: RequestBody?,
        paid_amount: RequestBody?,
        reduced_cost: RequestBody?,
        redeem_point: RequestBody?,
        old_voucher_paid_amount: MultipartBody.Part?,
        old_voucher_code:RequestBody?,
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
        old_stock_session_key: RequestBody?,
        old_stock_calc_type: RequestBody,
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
        sessionKey: RequestBody,
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
    ): Resource<List<PureGoldListDomain>>

    suspend fun updatePureGoldItems(
         gold_weight_ywae: List<MultipartBody.Part>?,
         maintenance_cost: List<MultipartBody.Part>?,
         threading_fees: List<MultipartBody.Part>?,
         type: List<MultipartBody.Part>?,
         wastage_ywae: List<MultipartBody.Part>?,
         sessionKey: String?
    ): Resource<String>

    suspend fun createPureGoldItems(
        gold_weight_ywae: String,
        maintenance_cost: String,
        threading_fees: String,
        type: String,
        wastage_ywae: String,
        sessionKey: String?
    ): Resource<String>

    suspend fun getGeneralSaleItems(
        sessionKey: String
    ): Resource<List<GeneralSaleListDomain>>

    suspend fun updateGeneralSaleItems(
         general_sale_item_id:  List<MultipartBody.Part>?,
         gold_weight_gm:  List<MultipartBody.Part>?,
         maintenance_cost:  List<MultipartBody.Part>?,
         qty:  List<MultipartBody.Part>?,
         wastage_ywae:  List<MultipartBody.Part>?,
         sessionKey: String?
    ): Resource<String>

    suspend fun createGeneralSaleItems(
        general_sale_item_id: String,
        gold_weight_gm: String,
        maintenance_cost: String,
        qty: String,
        wastage_ywae: String,
    ): Resource<String>

    suspend fun getUserRedeemPoints(
        userId: String
    ): Resource<String>

    suspend fun getRedeemMoney(
        redeemAmount: String
    ): Resource<String>

    suspend fun buyOldStock(): Resource<String>

}