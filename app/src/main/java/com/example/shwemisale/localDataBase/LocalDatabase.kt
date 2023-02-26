package com.example.shwemisale.localDataBase

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val ACCESS_TOKEN="access_token"
private const val CUSTOMER_ID="customer_id"


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

    fun saveCustomerId(token:String){
        sharedPref.edit{putString(ACCESS_TOKEN,token)}
    }

    fun removeCustomerId(){
        sharedPref.edit { remove(ACCESS_TOKEN) }
    }

    fun getAccessCustomerId():String?{
        val token =  sharedPref.getString(ACCESS_TOKEN, "0")
        return "$token"
    }

    fun clearSharedPreference(){
        removeToken()
        removeCustomerId()
    }
}