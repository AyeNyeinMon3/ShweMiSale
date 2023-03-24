package com.example.shwemisale.data_layers.dto.goldFromHome

import android.os.Parcelable
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GemWeightDetail(
    var id:Int = 0,
    var gem_qty: String,
    var gem_weight_gm_per_unit: String,
    val gem_weight_ywae_per_unit: String,
    var totalWeightKpy:String = "",
    var weightForOneK:String = "",
    var weightForOneP:String = "",
    var weightForOneY:String = "",
):Parcelable