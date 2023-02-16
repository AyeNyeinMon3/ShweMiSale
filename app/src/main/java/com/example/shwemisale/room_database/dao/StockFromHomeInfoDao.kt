package com.example.shwemisale.room_database.dao

import androidx.room.*
import com.example.shwemisale.room_database.entity.CustomerEntity
import com.example.shwemisale.room_database.entity.StockFromHomeInfoEntity

@Dao
interface StockFromHomeInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStockFromHomeInfoList(stockFromHomeList:List<StockFromHomeInfoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStockFromHomeInfoSingle(stockFromHomeList:StockFromHomeInfoEntity)

    @Update
    suspend fun updateStockFromHomeInfoSingle(stockFromHomeList:StockFromHomeInfoEntity)

    @Transaction
    @Query("SELECT * FROM `stock-from-home`")
    fun getStockFromHomeInfo(): List<StockFromHomeInfoEntity>

    @Transaction
    @Query("SELECT * FROM `stock-from-home` where id=:id")
    fun getStockFromHomeInfoById(id: String): StockFromHomeInfoEntity

    @Query("DELETE FROM `stock-from-home` where id=:id" )
    suspend fun deleteStockFromHomeInfo(id:String)

    @Query("DELETE FROM `stock-from-home`" )
    suspend fun deleteAllStockFromHomeInfo()

    @Query("update `stock-from-home` set rebuyPrice = :rebuyPrice where  id = :id")
    fun updateRebuyPrice(id: String, rebuyPrice: String)

    @Query("update `stock-from-home` set priceForPawn = :priceForPawn where  id = :id")
    fun updatePriceForPawn(id: String, priceForPawn: String)

    @Query("update `stock-from-home` set calculatedPriceForPawn = :calculatedPriceForPawn where  id = :id")
    fun updateCalculatedPriceForPawn(id: String, calculatedPriceForPawn: String)

    @Query("update `stock-from-home` set reduced_cost = :reducedCost where  id = :id")
    fun updateReducedCost(id: String,  reducedCost: String)
}