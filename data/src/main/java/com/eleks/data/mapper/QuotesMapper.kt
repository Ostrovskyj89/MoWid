package com.eleks.data.mapper

import com.eleks.data.model.QuoteDataModel
import com.eleks.data.model.SelectedQuoteDataModel
import com.eleks.domain.model.QuoteModel

fun QuoteDataModel.mapToDomain(selectedQuotes: List<SelectedQuoteDataModel>) = QuoteModel(
    id = id.orEmpty(),
    author = author.orEmpty(),
    created = created.orEmpty(),
    quote = quote.orEmpty(),
    isSelected = selectedQuotes.any { it.id == id }
)
