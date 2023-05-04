package com.example.shwemisale.data_layers.dto

data class RemainingAmountResponse(
    val data:RemainingAmountDto
)
data class RemainingAmountDto(
    val id:String?,
    val code:String?,
    val remaining_amount:String?
)