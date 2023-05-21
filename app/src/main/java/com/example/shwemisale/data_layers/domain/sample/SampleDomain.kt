package com.example.shwemisale.data_layers.domain.sample

data class SampleDomain(
    val localId:String?,
    val id: String?,
    val box_code: String?,
    val name: String?,
    val product_code: String?,
    val product_id: String?,
    var specification: String?,
    val thumbnail: String?,
    val weight_gm: String?,
    var isNew:Boolean,
    var isInventory:Boolean

)
