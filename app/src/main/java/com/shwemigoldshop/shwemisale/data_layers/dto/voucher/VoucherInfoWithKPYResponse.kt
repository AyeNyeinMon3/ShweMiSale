package com.shwemigoldshop.shwemisale.data_layers.dto.voucher

data class VoucherInfoWithKPYResponse(
    val data:VoucherInfoWithKPYDto
)

data class VoucherInfoWithKPYDto(
    val total_net_gold_weight_kpy:Double?,
    val total_wastage_kpy:Double?,
    val total_gold_weight_including_wastage_kpy:Double?,
    val total_gem_value:Long?,
    val total_pt_and_clip_cost:Long?,
    val total_maintenance_cost:Long?
)
