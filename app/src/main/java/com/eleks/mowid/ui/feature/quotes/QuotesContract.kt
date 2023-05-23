package com.eleks.mowid.ui.feature.quotes

import com.eleks.mowid.base.ui.UiEffect
import com.eleks.mowid.base.ui.UiEvent
import com.eleks.mowid.base.ui.UiState
import com.eleks.mowid.model.QuoteUIModel

data class QuotesState(
    val isLoading: Boolean,
    val quotes: List<QuoteUIModel>
) : UiState

sealed class QuotesEvent : UiEvent {
    data class QuoteItemChecked(
        val quoteId: String,
        val checked: Boolean,
        val quote: String,
        val author: String?
    ) : QuotesEvent()

    object ShowQuoteModal : QuotesEvent()
    object HideQuoteModal : QuotesEvent()
    object BackButtonClicked : QuotesEvent()
    data class AddQuoteClicked(val quote: String, val author: String) : QuotesEvent()
    data class OnItemDeleted(val id: String, val isSelected: Boolean) : QuotesEvent()
    data class OnEditClicked(
        val id: String,
        val editedQuote: String,
        val editedAuthor: String
    ) : QuotesEvent()
}

sealed class QuotesEffect : UiEffect {
    data class ShowError(val message: String) : QuotesEffect()
}
