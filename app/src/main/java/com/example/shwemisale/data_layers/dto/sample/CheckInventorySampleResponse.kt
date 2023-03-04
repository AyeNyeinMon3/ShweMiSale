package com.example.shwemisale.data_layers.dto.sample



data class CheckInventorySampleResponse(
    val data: SampleDto
)

data class SampleDto(
    val box_code: String?,
    val id: String?,
    val name: String?,
    val product_code: String?,
    val product_id: String?,
    val specification: String?,
    val thumbnail: String?,
    val weight_gm: String?
)