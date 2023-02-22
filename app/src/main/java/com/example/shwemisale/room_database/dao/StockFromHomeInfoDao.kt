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

    @Query("update `stock-from-home` set wastage_ywae = :wastageYwae where  id = :id")
    fun updateWastageYwae(id: String,  wastageYwae: String)

    @Query("update `stock-from-home` set oldStockCondition = :oldStockCondition where  id = :id")
    fun updateOldStockCondition(id: String,  oldStockCondition: String)

    @Query("update `stock-from-home` set oldStockGQinCarat = :oldStockGQinCarat where  id = :id")
    fun updateOldStockGQinCarat(id: String,  oldStockGQinCarat: String)

    @Query("update `stock-from-home` set oldStockImpurityWeightY = :oldStockImpurityWeightY where  id = :id")
    fun updateOldStockImpurityWeightY(id: String,  oldStockImpurityWeightY: String)

    @Query("update `stock-from-home` set oldStockABuyingPrice = :oldstockABuyingPrice where  id = :id")
    fun updateOldStockABuyingPrice(id: String,  oldstockABuyingPrice: String)

    @Query("update `stock-from-home` set oldStockb_voucher_buying_value = :bPrice where  id = :id")
    fun updateBprice(id: String,  bPrice: String)

    @Query("update `stock-from-home` set oldStockc_voucher_buying_value = :cPrice where  id = :id")
    fun updateCprice(id: String,  cPrice: String)

    @Query("update `stock-from-home` set oldStockDGoldWeightY = :dPrice where  id = :id")
    fun updateDprice(id: String,  dPrice: String)

    @Query("update `stock-from-home` set oldStockEPriceFromNewVoucher = :ePrice where  id = :id")
    fun updateEprice(id: String,  ePrice: String)

    @Query("update `stock-from-home` set oldStockFVoucherShownGoldWeightY = :fPrice where  id = :id")
    fun updateFprice(id: String,  fPrice: String)



}