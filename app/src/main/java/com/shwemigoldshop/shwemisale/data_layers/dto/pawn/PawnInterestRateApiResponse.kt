package com.shwemigoldshop.shwemisale.data_layers.dto.pawn

data class PawnInterestRateApiResponse(
    val data:List<PawnInterestRateDto>
)
data class PawnInterestRateDto(
    val range_from:String?,
    val range_to:String?,
    val rate:String?,
)
