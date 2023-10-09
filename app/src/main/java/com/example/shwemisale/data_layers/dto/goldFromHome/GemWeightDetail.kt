package com.example.shwemisale.data_layers.dto.goldFromHome

import android.os.Parcelable
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import kotlinx.android.parcel.Parcelize

data class GemWeightDetail(
    var id: String,
    var qty: String,
    var weight_gm_per_unit: String,
    var weight_ywae_per_unit: String,
    val session_key: String
)

data class GemWeightDetailResponse(
    val data: List<GemWeightDetail>
)

fun GemWeightDetail.asDomain(): GemWeightDetailDomain {
    return GemWeightDetailDomain(
        id,
        qty.toInt(),
        weight_gm_per_unit.toDouble(),
        weight_ywae_per_unit.toDouble(),
        sessionKey = session_key.orEmpty(),
        totalWeightYwae = if (weight_ywae_per_unit.toDouble() != 0.0) qty.toInt()*weight_ywae_per_unit.toDouble() else  qty.toInt()* getYwaeFromGram(weight_gm_per_unit.toDouble())
    )
}
