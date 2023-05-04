package com.example.shwemisale.data_layers.dto.goldFromHome

import android.os.Parcelable
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import kotlinx.android.parcel.Parcelize
data class GemWeightDetail(
    var id:Int = 0,
    var gem_qty: String,
    var gem_weight_gm_per_unit: String,
    var gem_weight_ywae_per_unit: String,
)

fun GemWeightDetail.asDomain():GemWeightDetailDomain{
    return GemWeightDetailDomain(
        id, gem_qty, gem_weight_gm_per_unit, gem_weight_ywae_per_unit
    )
}
