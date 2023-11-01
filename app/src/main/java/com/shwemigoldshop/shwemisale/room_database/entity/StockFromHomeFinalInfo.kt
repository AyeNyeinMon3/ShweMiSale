package com.shwemigoldshop.shwemisale.room_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stockFromHomeFinalInfo")
data class StockFromHomeFinalInfo(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val finalPawnPrice:String,
    val finalGoldWeightY:String,
    val finalVoucherPaidAmount:String
)