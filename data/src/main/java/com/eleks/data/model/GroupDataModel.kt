package com.eleks.data.model

data class GroupDataModel(
    var id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val quotes: List<QuoteDataModel>? = null
)
