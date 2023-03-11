package com.example.shwemisale.room_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shwemisale.data_layers.domain.sample.SampleDomain
import com.example.shwemisale.data_layers.dto.sample.SampleDto

@Entity(tableName = "samples")
data class SampleEntity(
    val id: String?,
    val box_code: String?,
    val name: String?,
    val product_code: String?,
    @PrimaryKey
    val product_id: String,
    val specification: String?,
    val thumbnail: String?,
    val weight_gm: String?,
    var isNew:Boolean?
)
fun SampleDomain.asEntity():SampleEntity{
    return SampleEntity(
        id.orEmpty(), box_code, name, product_code, product_id.orEmpty(), specification, thumbnail, weight_gm,isNew
    )
}

fun SampleEntity.asDomain():SampleDomain{
    return SampleDomain(
        id, box_code, name, product_code, product_id, specification, thumbnail, weight_gm,isNew?:false
    )
}