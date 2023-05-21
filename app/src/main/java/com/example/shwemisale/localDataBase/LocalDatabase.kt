package com.example.shwemisale.localDataBase

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val ACCESS_TOKEN="access_token"
private const val CUSTOMER_ID="customer_id"
private const val GENERAL_SALE_SESSION_KEY="general-sale-session_key"
private const val PURE_GOLD_SESSION_KEY="pure-gold-session_key"
private const val STOCK_FROM_HOME_SESSION_KEY="stock-from-home-session_key"
private const val PAWN_STOCK_FROM_HOME_SESSION_KEY="pawn-stock-from-home-session_key"
private const val TOTAL_PAWN_PRICE="total-pawn-price"
private const val TOTAL_GOLD_WEIGHT_YWAE="total-gold-weight-ywae"
private const val TOTAL_C_VOUCHER_BUYING_PRICE="total-c-voucher-buying-price"
private const val TOTAL_C_VOUCHER_BUYING_PRICE_FOR_PAWN="total-c-voucher-buying-price-for-pawn"
private const val TOTAL_PAWN_PRICE_FOR_REMAINED_PAWN_ITEMS="total-pawn-price-price-for-pawn-remained-items"


@Singleton
class LocalDatabase @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPref = context.getSharedPreferences("city_hr", Context.MODE_PRIVATE)

    fun saveToken(token:String){
        sharedPref.edit{putString(ACCESS_TOKEN,token)}
    }

    fun removeToken(){
        sharedPref.edit { remove(ACCESS_TOKEN) }
    }

    fun getAccessToken():String?{
        val token =  sharedPref.getString(ACCESS_TOKEN, "0")
        return "Bearer $token"
    }

    fun saveCustomerId(customerId:String){
        sharedPref.edit{putString(CUSTOMER_ID,customerId)}
    }

    fun removeCustomerId(){
        sharedPref.edit { remove(CUSTOMER_ID) }
    }


    fun getAccessCustomerId():String?{
        val customerId =  sharedPref.getString(CUSTOMER_ID, "")
        return "$customerId"
    }

    fun getGeneralSaleSessionKey():String?{
        val sessionKey =  sharedPref.getString(GENERAL_SALE_SESSION_KEY, "")
        return "$sessionKey"
    }

    fun saveGeneralSaleSessionKey(sessionKey:String){
        sharedPref.edit{putString(GENERAL_SALE_SESSION_KEY,sessionKey)}
    }

    fun removeGeneralSaleSessionKey(){
        sharedPref.edit { remove(GENERAL_SALE_SESSION_KEY) }
    }

    fun getPureGoldSessionKey():String?{
        val sessionKey =  sharedPref.getString(PURE_GOLD_SESSION_KEY, "")
        return "$sessionKey"
    }

    fun savePureGoldSessionKey(sessionKey:String){
        sharedPref.edit{putString(PURE_GOLD_SESSION_KEY,sessionKey)}
    }

    fun removePureGoldSessionKey(){
        sharedPref.edit { remove(PURE_GOLD_SESSION_KEY) }
    }

    fun getStockFromHomeSessionKey():String?{
        val sessionKey =  sharedPref.getString(STOCK_FROM_HOME_SESSION_KEY, "")
        return "$sessionKey"
    }

    fun saveStockFromHomeSessionKey(sessionKey:String){
        sharedPref.edit{putString(STOCK_FROM_HOME_SESSION_KEY,sessionKey)}
    }

    fun removeStockFromHomeSessionKey(){
        sharedPref.edit { remove(STOCK_FROM_HOME_SESSION_KEY) }
    }

    fun getTotalPawnPriceForStockFromHome():String{
        val price =  sharedPref.getString(TOTAL_PAWN_PRICE, "0")
        return "$price"
    }

    fun saveTotalPawnPriceForStockFromHome(price:String){
        sharedPref.edit{putString(TOTAL_PAWN_PRICE,price)}
    }

    fun removeTotalPawnPriceForStockFromHome(){
        sharedPref.edit { remove(TOTAL_PAWN_PRICE) }
    }

    fun getGoldWeightYwaeForStockFromHome():String{
        val goldweightYwae =  sharedPref.getString(TOTAL_GOLD_WEIGHT_YWAE, "0")
        return "$goldweightYwae"
    }

    fun saveGoldWeightYwaeForStockFromHome(ywae:String){
        sharedPref.edit{putString(TOTAL_GOLD_WEIGHT_YWAE,ywae)}
    }

    fun removeGoldWeightYwaeForStockFromHome(){
        sharedPref.edit { remove(TOTAL_GOLD_WEIGHT_YWAE) }
    }
    fun getTotalVoucherBuyingPriceForPawn():String{
        val price =  sharedPref.getString(TOTAL_C_VOUCHER_BUYING_PRICE_FOR_PAWN, "0")
        return "$price"
    }
    fun saveTotalVoucherBuyingPriceForPawn(price:String){
        sharedPref.edit{putString(TOTAL_C_VOUCHER_BUYING_PRICE_FOR_PAWN,price)}
    }
    fun removeTotalVoucherBuyingPriceForPawn(){
        sharedPref.edit { remove(TOTAL_C_VOUCHER_BUYING_PRICE_FOR_PAWN) }
    }
    fun getTotalCVoucherBuyingPriceForStockFromHome():String{
        val price =  sharedPref.getString(TOTAL_C_VOUCHER_BUYING_PRICE, "0")
        return "$price"
    }

    fun saveTotalCVoucherBuyingPriceForStockFromHome(price:String){
        sharedPref.edit{putString(TOTAL_C_VOUCHER_BUYING_PRICE,price)}
    }

    fun removeTotalCVoucherBuyingPriceForStockFromHome(){
        sharedPref.edit { remove(TOTAL_C_VOUCHER_BUYING_PRICE) }
    }

    fun getPawnOldStockSessionKey():String?{
        val sessionKey =  sharedPref.getString(PAWN_STOCK_FROM_HOME_SESSION_KEY, "")
        return "$sessionKey"
    }

    fun savePawnOldStockSessionKey(sessionKey:String){
        sharedPref.edit{putString(PAWN_STOCK_FROM_HOME_SESSION_KEY,sessionKey)}
    }

    fun removePawnOldStockSessionKey(){
        sharedPref.edit { remove(PAWN_STOCK_FROM_HOME_SESSION_KEY) }
    }


    fun getRemainedPawnItemsPrice():String{
        val price =  sharedPref.getString(TOTAL_PAWN_PRICE_FOR_REMAINED_PAWN_ITEMS, "0")
        return "$price"
    }

    fun saveRemainedPawnItemsPrice(price:String){
        sharedPref.edit{putString(TOTAL_PAWN_PRICE_FOR_REMAINED_PAWN_ITEMS,price)}
    }

    fun removeRemainedPawnItemsPrice(){
        sharedPref.edit { remove(TOTAL_PAWN_PRICE_FOR_REMAINED_PAWN_ITEMS) }
    }
    fun clearSharedPreference(){
        removeToken()
        removeCustomerId()
        removeGeneralSaleSessionKey()
        removePureGoldSessionKey()
        removeStockFromHomeSessionKey()
        removeTotalPawnPriceForStockFromHome()
        removeGoldWeightYwaeForStockFromHome()
        removeTotalCVoucherBuyingPriceForStockFromHome()
        removePawnOldStockSessionKey()
        removeTotalVoucherBuyingPriceForPawn()
    }
}