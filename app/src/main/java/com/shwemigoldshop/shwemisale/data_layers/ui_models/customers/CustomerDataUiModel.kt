package com.shwemigoldshop.shwemisale.data_layers.ui_models.customers

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerDataUiModel(
    val id:String,
    val code:String,
    val name:String,
    val phone:String,
    val date_of_birth:String,
    val gender:String,
    val address:String,
    val province_id:String,
    val township_id:String,
    val province_name:String,
    val township_name:String,
    val nrc:String
):Parcelable
