package com.example.shwemisale.network.api_services

import android.se.omapi.Session
import com.example.shwemisale.data_layers.dto.GeneralSaleApiResponse
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.StockFromHomForPawnResponse
import com.example.shwemisale.data_layers.dto.calculation.GoldPriceResponse
import com.example.shwemisale.data_layers.dto.customers.CustomerWhistListApiResponse
import com.example.shwemisale.data_layers.dto.generalSale.GeneralSaleListResponse
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.data_layers.dto.goldFromHome.Image
import com.example.shwemisale.data_layers.dto.goldFromHome.StockFromHomeResponse
import com.example.shwemisale.data_layers.dto.product.PureGoldListResponse
import com.example.shwemisale.data_layers.dto.product.SessionKeyResponse
import com.example.shwemisale.data_layers.dto.sample.CheckInventorySampleResponse
import com.example.shwemisale.data_layers.dto.sample.TakeInventorySampleResponse
import com.example.shwemisale.data_layers.dto.voucher.PaidAmountOfVoucherResponse
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithKPYResponse
import com.example.shwemisale.data_layers.dto.voucher.VoucherInfoWithValueResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface NormalSaleService {
    @GET("api/sales/normal/{voucherCode}/paid-amount")
    suspend fun getPaidAmountOfVoucher(
        @Header("Authorization") token: String,
        @Path("voucherCode") voucherCode: String,
        @Query("old_stock_session_key") old_stock_session_key: String?
    ): Response<PaidAmountOfVoucherResponse>

    @GET("api/sales/normal/get-gold-price")
    suspend fun getGoldPrices(
        @Header("Authorization") token: String,
        @Query("product_id[]") productIdList: List<String>
    ): Response<GoldPriceResponse>

    @GET("api/sales/normal/get-voucher-info-kpy")
    suspend fun getVoucherInfoWithKPY(
        @Header("Authorization") token: String,
        @Query("product_id[]") productIdList: List<String>
    ): Response<VoucherInfoWithKPYResponse>

    @GET("api/sales/normal/get-voucher-info-value")
    suspend fun getVoucherInfoWithValue(
        @Header("Authorization") token: String,
        @Query("product_id[]") productIdList: List<String>
    ): Response<VoucherInfoWithValueResponse>

    @JvmSuppressWildcards
    @POST("api/sales/normal/store/kpy")
    @Multipart
    suspend fun submitWithKPY(
        @Header("Authorization") token: String,
        @Part productIdList: List<MultipartBody.Part>?,
        @Part("user_id") user_id: RequestBody?,
        @Part("paid_amount") paid_amount: RequestBody?,
        @Part("reduced_cost") reduced_cost: RequestBody?,
        @Part("redeem_point") redeem_point: RequestBody?,
        @Part("old_voucher_code") old_voucher_code: RequestBody?,
        @Part old_voucher_paid_amount: MultipartBody.Part?,
        @Part("old_stock_session_key") old_stock_session_key: RequestBody,
        ): Response<SimpleResponse>

    @GET("api/old_stocks")
    suspend fun getStockFromHomeList(
        @Header("Authorization") token: String,
        @Query("session_key") sessionKey: String?
    ): Response<StockFromHomeResponse>

    @GET("api/pawn/{pawnVoucherCode}/old-stocks")
    suspend fun getStockFromHomeForPawn(
        @Header("Authorization") token: String,
        @Path("pawnVoucherCode") pawnVoucherCode: String
    ): Response<StockFromHomForPawnResponse>

    @JvmSuppressWildcards
    @Multipart
    @POST("api/old_stocks/create")
    suspend fun createStockFromHome(
        @Header("Authorization") token: String,
        @Part id:List<MultipartBody.Part>?,
        @Part a_buying_price: List<MultipartBody.Part?>?,
        @Part b_voucher_buying_value: List<MultipartBody.Part?>?,
        @Part c_voucher_buying_price: List<MultipartBody.Part?>?,
        @Part calculated_buying_value: List<MultipartBody.Part?>?,
        @Part calculated_for_pawn: List<MultipartBody.Part?>?,
        @Part d_gold_weight_ywae: List<MultipartBody.Part?>?,
        @Part e_price_from_new_voucher: List<MultipartBody.Part?>?,
        @Part f_voucher_shown_gold_weight_ywae: List<MultipartBody.Part?>?,
        @Part gem_value: List<MultipartBody.Part?>?,
        @Part gem_weight_details_qty: List<MultipartBody.Part?>?,
        @Part gem_weight_details_gm: List<MultipartBody.Part?>?,
        @Part gem_weight_details_ywae: List<MultipartBody.Part?>?,
        @Part gem_weight_ywae: List<MultipartBody.Part?>?,
        @Part gold_gem_weight_ywae: List<MultipartBody.Part?>?,
        @Part gold_weight_ywae: List<MultipartBody.Part?>?,
        @Part gq_in_carat: List<MultipartBody.Part?>?,
        @Part has_general_expenses: List<MultipartBody.Part?>?,
        @Part imageId: List<MultipartBody.Part?>?,
        @Part imageFile: List<MultipartBody.Part?>?,
        @Part impurities_weight_ywae: List<MultipartBody.Part?>?,
        @Part maintenance_cost: List<MultipartBody.Part?>?,
        @Part price_for_pawn: List<MultipartBody.Part?>?,
        @Part pt_and_clip_cost: List<MultipartBody.Part?>?,
        @Part qty: List<MultipartBody.Part?>?,
        @Part rebuy_price: List<MultipartBody.Part?>?,
        @Part size: List<MultipartBody.Part?>?,
        @Part stock_condition: List<MultipartBody.Part?>?,
        @Part stock_name: List<MultipartBody.Part?>?,
        @Part type: List<MultipartBody.Part?>?,
        @Part wastage_ywae: List<MultipartBody.Part?>?,
        @Part rebuy_price_vertical_option: List<MultipartBody.Part?>?,
        @Part productIdList: List<MultipartBody.Part?>?,
        @Part isEditable:List<MultipartBody.Part?>?,
        @Part isChecked:List<MultipartBody.Part?>?,
        @Part("session_key")sessionKey:RequestBody?
    ): Response<SessionKeyResponse>

    @Multipart
    @POST("api/old_stocks/update")
    suspend fun updateStockFromHome(
        @Header("Authorization") token: String,
        @Part id:List<MultipartBody.Part>?,
        @Part a_buying_price: List<MultipartBody.Part?>?,
        @Part b_voucher_buying_value: List<MultipartBody.Part?>?,
        @Part c_voucher_buying_price: List<MultipartBody.Part?>?,
        @Part calculated_buying_value: List<MultipartBody.Part?>?,
        @Part calculated_for_pawn: List<MultipartBody.Part?>?,
        @Part d_gold_weight_ywae: List<MultipartBody.Part?>?,
        @Part e_price_from_new_voucher: List<MultipartBody.Part?>?,
        @Part f_voucher_shown_gold_weight_ywae: List<MultipartBody.Part?>?,
        @Part gem_value: List<MultipartBody.Part?>?,
        @Part gem_weight_details_qty: List<MultipartBody.Part?>?,
        @Part gem_weight_details_gm: List<MultipartBody.Part?>?,
        @Part gem_weight_details_ywae: List<MultipartBody.Part?>?,
        @Part gem_weight_ywae: List<MultipartBody.Part?>?,
        @Part gold_gem_weight_ywae: List<MultipartBody.Part?>?,
        @Part gold_weight_ywae: List<MultipartBody.Part?>?,
        @Part gq_in_carat: List<MultipartBody.Part?>?,
        @Part has_general_expenses: List<MultipartBody.Part?>?,
        @Part imageId: List<MultipartBody.Part?>?,
        @Part imageFile: List<MultipartBody.Part?>?,
        @Part impurities_weight_ywae: List<MultipartBody.Part?>?,
        @Part maintenance_cost: List<MultipartBody.Part?>?,
        @Part price_for_pawn: List<MultipartBody.Part?>?,
        @Part pt_and_clip_cost: List<MultipartBody.Part?>?,
        @Part qty: List<MultipartBody.Part?>?,
        @Part rebuy_price: List<MultipartBody.Part?>?,
        @Part size: List<MultipartBody.Part?>?,
        @Part stock_condition: List<MultipartBody.Part?>?,
        @Part stock_name: List<MultipartBody.Part?>?,
        @Part type: List<MultipartBody.Part?>?,
        @Part wastage_ywae: List<MultipartBody.Part?>?,
        @Part rebuy_price_vertical_option: List<MultipartBody.Part?>?,
        @Part productIdList: List<MultipartBody.Part?>?,
        @Part isEditable: List<MultipartBody.Part?>?,
        @Part isChecked: List<MultipartBody.Part?>?,

        @Part("session_key")sessionKey:RequestBody?
    ): Response<SessionKeyResponse>

    @POST("api/sales/normal/store/value")
    @Multipart
    suspend fun submitWithValue(
        @Header("Authorization") token: String,
        @Part productIdList: List<MultipartBody.Part>?,
        @Part("user_id") user_id: RequestBody?,
        @Part("paid_amount") paid_amount: RequestBody?,
        @Part("reduced_cost") reduced_cost: RequestBody?,
        @Part("redeem_point") redeem_point: RequestBody?,
        @Part("old_voucher_code") old_voucher_code: RequestBody?,
        @Part old_voucher_paid_amount: MultipartBody.Part?,
        @Part("old_stock_session_key") old_stock_session_key: RequestBody,

        ): Response<SimpleResponse>


    /** New Order Sales */

    @POST("api/sales/order/store")
    @Multipart
    suspend fun submitOrderSale(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("gold_type_id") gold_type_id: RequestBody,
        @Part("gold_price") gold_price: RequestBody,
        @Part("total_gold_weight_ywae") total_gold_weight_ywae: RequestBody,
        @Part("est_unit_wastage_ywae") est_unit_wastage_ywae: RequestBody,
        @Part("qty") qty: RequestBody,
        @Part("gem_value") gem_value: RequestBody,
        @Part("maintenance_cost") maintenance_cost: RequestBody,
        @Part("date_of_delivery") date_of_delivery: RequestBody,
        @Part("remark") remark: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("paid_amount") paid_amount: RequestBody,
        @Part("reduced_cost") reduced_cost: RequestBody,
        @Part("old_stock_session_key") old_stock_session_key: RequestBody,
        @Part oldStockSampleListId: List<MultipartBody.Part>?
    ): Response<SimpleResponse>

    @JvmSuppressWildcards
    @POST("api/sales/pure-gold/store")
    @Multipart
    suspend fun submitPureGoldSale(
        @Header("Authorization") token: String,
        @Part("session_key") sessionKey: RequestBody?,
        @Part("gold_price") gold_price: RequestBody?,
        @Part("user_id") user_id: RequestBody?,
        @Part("paid_amount") paid_amount: RequestBody?,
        @Part("reduced_cost") reduced_cost: RequestBody?,
        @Part("old_stock_session_key") old_stock_session_key: RequestBody,


        ): Response<SimpleResponse>

    @POST("api/sales/general/store")
    @Multipart
    suspend fun submitGeneralSale(
        @Header("Authorization") token: String,
        @Part("session_key") session_key: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("paid_amount") paid_amount: RequestBody,
        @Part("reduced_cost") reduced_cost: RequestBody,
        @Part("old_stock_session_key") old_stock_session_key: RequestBody,


        ): Response<SimpleResponse>


    @GET("api/general-sale-items")
    suspend fun getGeneralSalesItems(
        @Header("Authorization") token: String,
    ): Response<GeneralSaleApiResponse>

    @GET("api/products/{productId}/check-samples")
    suspend fun checkInventorySample(
        @Header("Authorization") token: String,
        @Path("productId") productId: String
    ): Response<CheckInventorySampleResponse>

    @FormUrlEncoded
    @POST("api/samples/inventory/take")
    suspend fun saveSample(
        @Header("Authorization") token: String,
        @FieldMap sample: HashMap<String, String>
    ): Response<TakeInventorySampleResponse>

    @Multipart
    @POST("api/samples/outside/take")
    suspend fun saveOutsideSample(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody?,
        @Part("weight_gm") weight_gm: RequestBody?,
        @Part("specification") specification: RequestBody?,
        @Part image: MultipartBody.Part
    ): Response<CheckInventorySampleResponse>

    @GET("api/sales/pure-gold/items")
    suspend fun getPureGoldItems(
        @Header("Authorization") token: String,
        @Query("session_key") sessionKey: String
    ): Response<PureGoldListResponse>

    @POST("api/sales/pure-gold/items/create")
    @Multipart
    suspend fun createPureGoldSaleItem(
        @Header("Authorization") token: String,
        @Part("gold_weight_ywae") gold_weight_ywae: RequestBody?,
        @Part("maintenance_cost") maintenance_cost: RequestBody?,
        @Part("threading_fees") threading_fees: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part("wastage_ywae") wastage_ywae: RequestBody?,
        @Part("session_key") session_key: RequestBody?

    ): Response<SessionKeyResponse>

    @Multipart
    @POST("api/sales/pure-gold/items/update")
    suspend fun updatePureGoldSaleItem(
        @Header("Authorization") token: String,
        @Part gold_weight_ywae: List<MultipartBody.Part>?,
        @Part maintenance_cost: List<MultipartBody.Part>?,
        @Part threading_fees: List<MultipartBody.Part>?,
        @Part type: List<MultipartBody.Part>?,
        @Part wastage_ywae: List<MultipartBody.Part>?,
        @Part("session_key") session_key: RequestBody?
    ): Response<SessionKeyResponse>

    @GET("api/sales/general/items")
    suspend fun getGeneralSaleItems(
        @Header("Authorization") token: String,
        @Query("session_key") sessionKey: String
    ): Response<GeneralSaleListResponse>

    @Multipart
    @POST("api/sales/general/items/create")
    suspend fun createGeneralSaleItem(
        @Header("Authorization") token: String,
        @Part("general_sale_item_id") general_sale_item_id: RequestBody?,
        @Part("qty") qty: RequestBody?,
        @Part("gold_weight_gm") gold_weight_gm: RequestBody?,
        @Part("wastage_ywae") wastage_ywae: RequestBody?,
        @Part("maintenance_cost") maintenance_cost: RequestBody?,
        @Part("session_key") session_key: RequestBody?

    ): Response<SessionKeyResponse>

    @Multipart
    @POST("api/sales/general/items/update")
    suspend fun updateGeneralSaleItem(
        @Header("Authorization") token: String,
        @Part general_sale_item_id: List<MultipartBody.Part>?,
        @Part qty: List<MultipartBody.Part>?,
        @Part gold_weight_gm: List<MultipartBody.Part>?,
        @Part wastage_ywae: List<MultipartBody.Part>?,
        @Part maintenance_cost: List<MultipartBody.Part>?,
        @Part("session_key") session_key: RequestBody?
    ): Response<SessionKeyResponse>

    @GET("api/users/{userId}/get-points")
    suspend fun getUserRedeemPoints(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<SessionKeyResponse>

    @GET("api/points/{redeemAmount}/get-discount-amount")
    suspend fun getRedeemAmount(
        @Header("Authorization") token: String,
        @Path("redeemAmount") redeemAmount: String
    ): Response<SessionKeyResponse>

    @FormUrlEncoded
    @POST("api/rebuys/store")
    suspend fun buyOldStocks(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String?,
        @Field("old_stock_session_key") old_stock_session_key: String?
    ): Response<SimpleResponse>

}