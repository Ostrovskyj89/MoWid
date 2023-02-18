package com.eleks.data.model

@kotlinx.serialization.Serializable
data class QuoteDataModel(
    val id: String? = null,
    val quote: String? = null,
    val author: String? = null,
    val created: String? = null
)
