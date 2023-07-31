package com.example.shwemisale.data_layers.dto.goldFromHome

import android.os.Parcelable
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import kotlinx.android.parcel.Parcelize
data class GemWeightDetail(
    var id:String,
    var qty: String,
    var weight_gm_per_unit: String,
    var weight_ywae_per_unit: String,
    val session_key:String
)

data class GemWeightDetailResponse(
    val data:List<GemWeightDetail>
)

fun GemWeightDetail.asDomain():GemWeightDetailDomain{
    return GemWeightDetailDomain(
        id, qty, weight_gm_per_unit, weight_ywae_per_unit, sessionKey = session_key
    )
}
