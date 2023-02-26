package com.example.shwemisale.repository

import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateApiResponse
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateDto
import com.example.shwemisale.data_layers.dto.pawn.PawnVoucherScanDto
import com.example.shwemisale.data_layers.dto.pawn.PawnVoucherScanResponse
import com.example.shwemisale.network.api_services.PawnService
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PawnRepository {

    suspend fun getPawnInterestRate(
    ): Resource<List<PawnInterestRateDto>>


    suspend fun getPawnVoucherScan(
        voucherCode:String,
    ): Resource<PawnVoucherScanDto>


    suspend fun storePawn(
        user_id:String?,
        total_debt_amount:String?,
        interest_rate:String?,
        warning_period_months:String?,
        interest_free_from:String?,
        interest_free_to:String?,

        old_stocks_nameList:List<MultipartBody.Part>?,
        oldStockImageIds:List<MultipartBody.Part>?,
        oldStockImageFile:List<MultipartBody.Part>?,
        oldStockCondition:List<MultipartBody.Part>?,

        oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        oldStockGemWeightY:List<MultipartBody.Part>?,

        oldStockImpurityWeightY:List<MultipartBody.Part>?,

        oldStockGoldWeightY:List<MultipartBody.Part>?,

        oldStockWastageWeightY:List<MultipartBody.Part>?,

        oldStockRebuyPrice:List<MultipartBody.Part>?,
        oldStockGQinCarat:List<MultipartBody.Part>?,
        oldStockMaintenance_cost:List<MultipartBody.Part>?,
        oldStockGemValue:List<MultipartBody.Part>?,
        oldStockPTAndClipCost:List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        oldStockPriceForPawn:List<MultipartBody.Part>?,
        oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        oldStockABuyingPrice:List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        oldStockDGoldWeightY:List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,

        ): Resource<String>


    suspend fun createPrepaidDebt(
        voucherCode:String,
        prepaid_debt:String,

        reduced_amount:String,
    ): Resource<String>

    suspend fun createPrepaidInterest(
        voucherCode:String,

        number_of_months:String,
        reduced_amount:String,
    ): Resource<String>


    suspend fun increaseDebt(
        voucherCode:String,

        increased_debt:String,
        reduced_amount:String,

        old_stocks_nameList:List<MultipartBody.Part>?,
        oldStockImageIds:List<MultipartBody.Part>?,
        oldStockImageFile:List<MultipartBody.Part>?,
        oldStockCondition:List<MultipartBody.Part>?,

        oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        oldStockGemWeightY:List<MultipartBody.Part>?,

        oldStockImpurityWeightY:List<MultipartBody.Part>?,

        oldStockGoldWeightY:List<MultipartBody.Part>?,

        oldStockWastageWeightY:List<MultipartBody.Part>?,

        oldStockRebuyPrice:List<MultipartBody.Part>?,
        oldStockGQinCarat:List<MultipartBody.Part>?,
        oldStockMaintenance_cost:List<MultipartBody.Part>?,
        oldStockGemValue:List<MultipartBody.Part>?,
        oldStockPTAndClipCost:List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        oldStockPriceForPawn:List<MultipartBody.Part>?,
        oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        oldStockABuyingPrice:List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        oldStockDGoldWeightY:List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,
    ): Resource<String>


    suspend fun payInterestAndIncreaseDebt(
        voucherCode:String,
        increased_debt:String,
        reduced_amount:String,

        old_stocks_nameList:List<MultipartBody.Part>?,
        oldStockImageIds:List<MultipartBody.Part>?,
        oldStockImageFile:List<MultipartBody.Part>?,
        oldStockCondition:List<MultipartBody.Part>?,

        oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        oldStockGemWeightY:List<MultipartBody.Part>?,

        oldStockImpurityWeightY:List<MultipartBody.Part>?,

        oldStockGoldWeightY:List<MultipartBody.Part>?,

        oldStockWastageWeightY:List<MultipartBody.Part>?,

        oldStockRebuyPrice:List<MultipartBody.Part>?,
        oldStockGQinCarat:List<MultipartBody.Part>?,
        oldStockMaintenance_cost:List<MultipartBody.Part>?,
        oldStockGemValue:List<MultipartBody.Part>?,
        oldStockPTAndClipCost:List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        oldStockPriceForPawn:List<MultipartBody.Part>?,
        oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        oldStockABuyingPrice:List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        oldStockDGoldWeightY:List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,
    ): Resource<String>



    suspend fun payInterest(
        voucherCode:String,

        reduced_amount:String,
    ): Resource<String>


    suspend fun payInterestAndSettleDebt(
        voucherCode:String,

        reduced_amount:String,

        debt:String,
    ): Resource<String>


    suspend fun payInterestAndReturnStock(
        voucherCode:String,
        reduced_amount:String,
        debt:String,

        old_stock_id:String,
    ): Resource<String>


    suspend fun settle(
        voucherCode:String,
        reduced_amount:String,
    ): Resource<String>


    suspend fun sellOldStock(
        voucherCode:String,
        reduced_amount:String,

        old_stocks_nameList:List<MultipartBody.Part>?,
        oldStockImageIds:List<MultipartBody.Part>?,
        oldStockImageFile:List<MultipartBody.Part>?,
        oldStockCondition:List<MultipartBody.Part>?,

        oldStockGoldGemWeightY:List<MultipartBody.Part>?,

        oldStockGemWeightY:List<MultipartBody.Part>?,

        oldStockImpurityWeightY:List<MultipartBody.Part>?,

        oldStockGoldWeightY:List<MultipartBody.Part>?,

        oldStockWastageWeightY:List<MultipartBody.Part>?,

        oldStockRebuyPrice:List<MultipartBody.Part>?,
        oldStockGQinCarat:List<MultipartBody.Part>?,
        oldStockMaintenance_cost:List<MultipartBody.Part>?,
        oldStockGemValue:List<MultipartBody.Part>?,
        oldStockPTAndClipCost:List<MultipartBody.Part>?,
        oldStockCalculatedBuyingValue:List<MultipartBody.Part>?,
        oldStockPriceForPawn:List<MultipartBody.Part>?,
        oldStockCalculatedForPawn:List<MultipartBody.Part>?,

        oldStockABuyingPrice:List<MultipartBody.Part>?,
        oldStockb_voucher_buying_value:List<MultipartBody.Part>?,
        oldStockc_voucher_buying_price:List<MultipartBody.Part>?,

        oldStockDGoldWeightY:List<MultipartBody.Part>?,

        oldStockEPriceFromNewVoucher:List<MultipartBody.Part>?,

        oldStockFVoucherShownGoldWeightY:List<MultipartBody.Part>?,
    ): Resource<String>
}