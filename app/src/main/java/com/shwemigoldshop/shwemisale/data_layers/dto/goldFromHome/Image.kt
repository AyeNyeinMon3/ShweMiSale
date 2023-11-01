package com.shwemigoldshop.shwemisale.data_layers.dto.goldFromHome

import android.os.Parcelable
import com.shwemigoldshop.shwemisale.data_layers.ShweMiFile
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    val id: String?,
    val type: String?,
    val url: String?
):Parcelable

fun ShweMiFile.asImage():Image{
    return Image(
        id,
        type,
        url
    )
}