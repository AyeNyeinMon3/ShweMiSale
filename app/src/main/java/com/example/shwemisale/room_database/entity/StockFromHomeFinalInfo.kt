package com.example.shwemisale.room_database.entity

import androidx.room.Entity

@Entity(tableName = "stockFromHomeFinalInfo")
data class StockFromHomeFinalInfo(
    val finalPawnPrice:String,
    val finalGoldWeightY:String,
    val finalVoucherPaidAmount:String
)