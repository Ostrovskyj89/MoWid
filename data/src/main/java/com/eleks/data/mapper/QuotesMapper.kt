package com.eleks.data.mapper

import com.eleks.data.model.QuoteDataModel
import com.eleks.data.model.SelectedQuoteDataModel
import com.eleks.domain.model.QuoteModel

fun QuoteDataModel.mapToDomain(selectedQuotes: List<SelectedQuoteDataModel>) = QuoteModel(
    id = id.orEmpty(),
    author = author.orEmpty(),
    created = created.orEmpty(),
    quote = quote.orEmpty(),
    canBeDeleted = canBeDeleted ?: false,
    isSelected = isQuoteSelected(this, selectedQuotes)
)

fun isQuoteSelected(
    quoteDataModel: QuoteDataModel,
    selectedQuotes: List<SelectedQuoteDataModel>
): Boolean {
    return selectedQuotes.firstOrNull { quoteDataModel.id == it.id } != null
}
