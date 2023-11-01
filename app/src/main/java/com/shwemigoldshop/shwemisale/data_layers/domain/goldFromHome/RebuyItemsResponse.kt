package com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome

data class RebuyItemsResponse(
    val data:List<RebuyItemDto>
)
data class RebuyItemDto(
    val id:String,
    val name:String,
    val size:String,
    val qty:Int,
    val isEditing:Boolean = false,
    val hasNameModified:Boolean,
    val canEdit:Boolean = true
){
    val isMinusEnable = qty>0

}

data class RebuyItemWithSize(
    val smallSizeItems:List<RebuyItemDto>,
    val largeSizeItems:List<RebuyItemDto>
)
