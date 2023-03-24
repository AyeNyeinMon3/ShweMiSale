package com.example.shwemisale.localDataBase

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val ACCESS_TOKEN="access_token"
private const val CUSTOMER_ID="customer_id"
private const val SESSION_KEY="session_key"
private const val STOCK_FROM_HOME_SESSION_KEY="stock-from-home-session_key"
private const val TOTAL_PAWN_PRICE="total-pawn-price"
private const val TOTAL_GOLD_WEIGHT_YWAE="total-gold-weight-ywae"
private const val TOTAL_C_VOUCHER_BUYING_PRICE="total-c-voucher-buying-price"


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

    fun getSessionKey():String?{
        val sessionKey =  sharedPref.getString(SESSION_KEY, "")
        return "$sessionKey"
    }

    fun saveSessionKey(sessionKey:String){
        sharedPref.edit{putString(SESSION_KEY,sessionKey)}
    }

    fun removeSessionKey(){
        sharedPref.edit { remove(SESSION_KEY) }
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

    fun getTotalPawnPriceForStockFromHome():String?{
        val price =  sharedPref.getString(TOTAL_PAWN_PRICE, "")
        return "$price"
    }

    fun saveTotalPawnPriceForStockFromHome(price:String){
        sharedPref.edit{putString(TOTAL_PAWN_PRICE,price)}
    }

    fun removeTotalPawnPriceForStockFromHome(){
        sharedPref.edit { remove(TOTAL_PAWN_PRICE) }
    }

    fun getGoldWeightYwaeForStockFromHome():String?{
        val goldweightYwae =  sharedPref.getString(TOTAL_GOLD_WEIGHT_YWAE, "")
        return "$goldweightYwae"
    }

    fun saveGoldWeightYwaeForStockFromHome(ywae:String){
        sharedPref.edit{putString(TOTAL_GOLD_WEIGHT_YWAE,ywae)}
    }

    fun removeGoldWeightYwaeForStockFromHome(){
        sharedPref.edit { remove(TOTAL_GOLD_WEIGHT_YWAE) }
    }

    fun getTotalCVoucherBuyingPriceForStockFromHome():String?{
        val price =  sharedPref.getString(TOTAL_C_VOUCHER_BUYING_PRICE, "")
        return "$price"
    }

    fun saveTotalCVoucherBuyingPriceForStockFromHome(price:String){
        sharedPref.edit{putString(TOTAL_C_VOUCHER_BUYING_PRICE,price)}
    }

    fun removeTotalCVoucherBuyingPriceForStockFromHome(){
        sharedPref.edit { remove(TOTAL_C_VOUCHER_BUYING_PRICE) }
    }


    fun clearSharedPreference(){
        removeToken()
        removeCustomerId()
        removeSessionKey()
        removeStockFromHomeSessionKey()
        removeTotalPawnPriceForStockFromHome()
        removeGoldWeightYwaeForStockFromHome()
        removeTotalCVoucherBuyingPriceForStockFromHome()
    }
}