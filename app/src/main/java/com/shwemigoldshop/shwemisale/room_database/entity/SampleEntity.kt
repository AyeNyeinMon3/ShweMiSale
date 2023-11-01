package com.shwemigoldshop.shwemisale.room_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shwemigoldshop.shwemisale.data_layers.domain.sample.SampleDomain
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

@Entity(tableName = "samples")
data class SampleEntity(
    @PrimaryKey
    val localId:String,
    val id: String?,
    val box_code: String?,
    val name: String?,
    val product_code: String?,
    val product_id: String,
    val specification: String?,
    val thumbnail: String?,
    val weight_gm: String?,
    var isNew:Boolean?,
    var isInventory:Boolean
)
fun SampleDomain.asEntity():SampleEntity{
    return SampleEntity(
        LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString(),id, box_code, name, product_code, product_id.orEmpty(), specification, thumbnail, weight_gm,isNew,isInventory
    )
}

fun SampleEntity.asDomain():SampleDomain{
    return SampleDomain(
        localId,id, box_code, name, product_code, product_id, specification, thumbnail, weight_gm,isNew?:false,isInventory
    )
}