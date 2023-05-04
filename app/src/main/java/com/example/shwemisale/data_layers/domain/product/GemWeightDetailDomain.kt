package com.example.shwemisale.data_layers.domain.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GemWeightDetailDomain(
    var id:Int = 0,
    var gem_qty: String,
    var gem_weight_gm_per_unit: String,
    var gem_weight_ywae_per_unit: String,
    var totalWeightYwae:String = "0.0",
    var weightForOneK:String = "0",
    var weightForOneP:String = "0",
    var weightForOneY:String = "0.0",
): Parcelable