package com.eleks.mowid.model

import com.eleks.domain.model.QuoteModel

data class QuoteUIModel(
    val id: String,
    val author: String,
    val created: String,
    val quote: String,
    val isSelected: Boolean
)

fun QuoteUIModel.toDomainModel() = QuoteModel(
    id = id,
    author = author,
    created = created,
    quote = quote,
    isSelected = isSelected
)

fun QuoteModel.toUIModel() = QuoteUIModel(
    id = id,
    author = author,
    created = created,
    quote = quote,
    isSelected = isSelected
)

fun List<QuoteUIModel>.toDomainModel() = map { it.toDomainModel() }

fun List<QuoteModel>.toUIModel() = map { it.toUIModel() }
