package com.example.shwemisale.room_database.dao

import androidx.room.*
import com.example.shwemisale.room_database.entity.CustomerEntity
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo

@Dao
interface StockFromHomeFinalInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStockFromHomeFinalInfo(stockFromHomeFinalInfo: StockFromHomeFinalInfo)

    @Transaction
    @Query("SELECT * FROM stockFromHomeFinalInfo")
    fun getStockFromHomeFinalInfo(): StockFromHomeFinalInfo

    @Query("DELETE FROM stockFromHomeFinalInfo")
    suspend fun deleteStockFromHomeFinalInfo()
}