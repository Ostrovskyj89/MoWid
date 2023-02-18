package com.eleks.data.model

@kotlinx.serialization.Serializable
data class GroupDataModel(
    var id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val quotes: List<QuoteDataModel>? = null
)
