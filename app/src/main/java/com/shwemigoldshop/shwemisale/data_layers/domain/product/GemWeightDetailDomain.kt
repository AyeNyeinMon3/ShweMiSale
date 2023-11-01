package com.shwemigoldshop.shwemisale.data_layers.domain.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GemWeightDetailDomain(
    val id:String,
    val gem_qty: Int,
    val gem_weight_gm_per_unit: Double,
    val gem_weight_ywae_per_unit: Double,
    val totalWeightYwae:Double,
    val sessionKey:String?
): Parcelable{
    val isCalculatable = gem_qty>0
}