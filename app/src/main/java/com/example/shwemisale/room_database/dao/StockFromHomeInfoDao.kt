package com.example.shwemisale.room_database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.shwemisale.data_layers.ShweMiFile
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
    fun getStockFromHomeInfo(): LiveData<List<StockFromHomeInfoEntity>>

    @Transaction
    @Query("SELECT * FROM `stock-from-home` where id=:id")
    fun getStockFromHomeInfoById(id: String): StockFromHomeInfoEntity

    @Delete
    suspend fun deleteStockFromHomeInfo(item:StockFromHomeInfoEntity)

    @Query("DELETE FROM `stock-from-home`" )
    suspend fun deleteAllStockFromHomeInfo()

    @Query("update `stock-from-home` set image = :image where  id = :id")
    fun updateImage(id: String, image: String)

    @Query("update `stock-from-home` set imageId = :imageId where  id = :id")
    fun updateImageId(id: String, imageId: String?)

    @Query("update `stock-from-home` set name = :name where  id = :id")
    fun updateName(id: String, name: String)

    @Query("update `stock-from-home` set qty = :qty where  id = :id")
    fun updateQty(id: String, qty: String)

    @Query("update `stock-from-home` set size = :size where  id = :id")
    fun updateSize(id: String, size: String)

    @Query("update `stock-from-home` set derived_net_gold_weight_ywae = :derived_net_gold_weight_ywae where  id = :id")
    fun updateDerivedNetGoldWeightYwae(id: String, derived_net_gold_weight_ywae: String)

    @Query("update `stock-from-home` set gem_value = :gemValue where  id = :id")
    fun updateGemValue(id: String, gemValue: String)

    @Query("update `stock-from-home` set gem_weight_ywae = :gemWeightYwae where  id = :id")
    fun updateGemWeightYwae(id: String, gemWeightYwae: String)

    @Query("update `stock-from-home` set gold_and_gem_weight_gm = :gold_and_gem_weight_gm where  id = :id")
    fun updateGoldAndGemWeightGm(id: String, gold_and_gem_weight_gm: String)

    @Query("update `stock-from-home` set gold_price = :goldPrice where  id = :id")
    fun updateGoldPrice(id: String, goldPrice: String)

    @Query("update `stock-from-home` set maintenance_cost = :maintenance_cost where  id = :id")
    fun updateMaintenanceCost(id: String, maintenance_cost: String)

    @Query("update `stock-from-home` set pt_and_clip_cost = :pt_and_clip_cost where  id = :id")
    fun updatePtAndClipCost(id: String, pt_and_clip_cost: String)

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

    @Query("update `stock-from-home` set oldStockDGoldWeightY = :oldStockDGoldWeightY where  id = :id")
    fun updateDprice(id: String,  oldStockDGoldWeightY: String)

    @Query("update `stock-from-home` set oldStockEPriceFromNewVoucher = :ePrice where  id = :id")
    fun updateEprice(id: String,  ePrice: String)

    @Query("update `stock-from-home` set oldStockFVoucherShownGoldWeightY = :oldStockFVoucherShownGoldWeightY where  id = :id")
    fun updateFprice(id: String,  oldStockFVoucherShownGoldWeightY: String)

    @Query("update `stock-from-home` set goldWeightYwae = :goldWeightYwae where  id = :id")
    fun updateGoldWeighYwae(id: String,  goldWeightYwae: String)

    @Query("update `stock-from-home` set gem_details_qty = :gem_details_qty where  id = :id")
    fun updateGemDetailsQty(id: String,  gem_details_qty: List<String>)

    @Query("update `stock-from-home` set gem_details_gm_per_units = :gem_details_gm_per_units where  id = :id")
    fun updateGemDetailsGmPerUnits(id: String,  gem_details_gm_per_units: List<String>)

    @Query("update `stock-from-home` set gem_details_ywae_per_units = :gem_details_ywae_per_units where  id = :id")
    fun updateGemDetailsYwaePerUnits(id: String,  gem_details_ywae_per_units: List<String>)



//val derived_gold_type_id: String?,
//
//    val derived_net_gold_weight_kpy: String?,
//
//    val derived_net_gold_weight_ywae: String?,
//
//    val gem_value: String?,
//
//
//    val gem_weight_ywae: String?,
//
//    val gold_and_gem_weight_gm: String?,
//
//    val gold_price: String?,
//    val image: String?,
//    val maintenance_cost: String?,
//    val name: String?,
//    val pt_and_clip_cost: String?,

}