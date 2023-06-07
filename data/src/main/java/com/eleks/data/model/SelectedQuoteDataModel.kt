package com.eleks.data.model

data class SelectedQuoteDataModel(
    val id: String? = null,
    val groupId: String? = null,
    val quote: String? = null,
    val author: String? = null,
    val memeUrl: String? = null,
    val shownAt: Long? = null,
    var selectedBy: String? = null
)
