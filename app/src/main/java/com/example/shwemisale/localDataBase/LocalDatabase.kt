package com.example.shwemisale.localDataBase

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val ACCESS_TOKEN="access_token"
private const val CUSTOMER_ID="customer_id"
private const val SESSION_KEY="session_key"


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

    fun clearSharedPreference(){
        removeToken()
        removeCustomerId()
        removeSessionKey()
    }
}