package com.example.shwemisale.repository

import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.dto.RemainingAmountDto
import com.example.shwemisale.data_layers.dto.pawn.PawnInterestRateDto
import com.example.shwemisale.data_layers.dto.pawn.PawnVoucherScanDto

interface PawnRepository {

    suspend fun getPawnInterestRate(
        amount:String
    ): Resource<PawnInterestRateDto>


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
        is_app_functions_allowed:String?,
        old_session_key:String
    ): Resource<String>


    suspend fun createPrepaidDebt(
        voucherCode:String,
        prepaid_debt:String,
        reduced_amount:String,
        is_app_functions_allowed:String?,

        ): Resource<String>

    suspend fun createPrepaidInterest(
        voucherCode:String,
        number_of_months:String,
        reduced_amount:String,
        is_app_functions_allowed:String?,

        ): Resource<String>


    suspend fun increaseDebt(
        voucherCode:String,

        increased_debt:String,
        reduced_amount:String,
        is_app_functions_allowed:String?,
        old_session_key:String
    ): Resource<String>


    suspend fun payInterestAndIncreaseDebt(
        voucherCode:String,
        increased_debt:String,
        reduced_amount:String,
        is_app_functions_allowed:String?,
        old_session_key:String

    ): Resource<String>



    suspend fun payInterest(
        voucherCode:String,

        reduced_amount:String,
        is_app_functions_allowed:String?,
        ): Resource<String>


    suspend fun payInterestAndSettleDebt(
        voucherCode:String,

        reduced_amount:String,

        debt:String,
        is_app_functions_allowed:String?,
        ): Resource<String>


    suspend fun payInterestAndReturnStock(
        voucherCode:String,
        reduced_amount:String,
        debt:String,
        is_app_functions_allowed:String?,

        old_stock_id:List<String>,
    ): Resource<String>


    suspend fun settle(
        voucherCode:String,
        reduced_amount:String,
        is_app_functions_allowed:String?,

        ): Resource<String>


    suspend fun sellOldStock(
        voucherCode:String,
        reduced_amount:String,
        is_app_functions_allowed:String?,
        old_stock_id:List<String>,

    ): Resource<String>

    suspend fun payBalance(
       sale_id:String,
       paid_amount:String?,
    ): Resource<String>

    suspend fun getRemainingAmount(
        saleCode:String?,
    ): Resource<RemainingAmountDto>
}