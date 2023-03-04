package com.example.shwemisale.data_layers.domain.goldFromHome

data class RebuyItemsResponse(
    val data:List<RebuyItemDto>
)
data class RebuyItemDto(
    val id:String,
    val name:String,
    val size:String,
    var qty:Int = 0
)
