package com.shwemigoldshop.shwemisale.pagingSource

import com.squareup.moshi.Json

data class PagingMeta(
    @field:Json(name = "current_page")
    val currentPage: Int?,
    @field:Json(name = "last_page")
    val lastPage: Int?,
    val from: Int?,
    val to: Int?,
    val total: Int?
)