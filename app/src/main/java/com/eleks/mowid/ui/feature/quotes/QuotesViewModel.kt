package com.eleks.mowid.ui.feature.quotes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.eleks.domain.intearactor.MotivationPhraseInteractor
import com.eleks.mowid.base.ui.BaseViewModel
import com.eleks.mowid.model.toUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val motivationPhraseInteractor: MotivationPhraseInteractor,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<QuotesState, QuotesEvent, QuotesEffect>() {

    private val groupId = savedStateHandle.get<String>("group_id").orEmpty()

    init {
        motivationPhraseInteractor.getQuotesListFlow(groupId)
            .onStart {
                setState { copy(isLoading = true) }
            }
            .flowOn(Dispatchers.IO)
            .onEach { data ->
                setState {
                    copy(
                        isLoading = false,
                        quotes = data.toUIModel()
                    )
                }
            }
            .onCompletion {
                setState { copy(isLoading = false) }
            }
            .catch {
                QuotesEffect.ShowError(
                    message = it.message.toString()
                ).sendEffect()
            }
            .launchIn(viewModelScope)
    }

    private fun deleteQuote(quoteId: String, isSelected: Boolean) {
        viewModelScope.launch {
            motivationPhraseInteractor.deleteQuote(groupId, quoteId, isSelected)
        }
    }

    override fun handleEvent(event: QuotesEvent) {
        when (event) {
            is QuotesEvent.AddQuoteClicked -> {
                viewModelScope.launch {
                    motivationPhraseInteractor.addQuote(
                        groupId = groupId,
                        quote = event.quote,
                        author = event.author
                    )
                }
            }
            QuotesEvent.HideAddQuoteModal -> {}
            is QuotesEvent.QuoteItemChecked -> {
                viewModelScope.launch {
                    motivationPhraseInteractor.saveSelection(
                        groupId = groupId,
                        quoteId = event.quoteId,
                        quote = event.quote,
                        author = event.author,
                        isSelected = event.checked
                    )
                }
            }
            QuotesEvent.ShowAddQuoteModal -> {}
            QuotesEvent.BackButtonClicked -> {}
            is QuotesEvent.OnItemDeleted -> deleteQuote(event.id, event.isSelected)
        }
    }

    override fun createInitialState(): QuotesState = QuotesState(
        isLoading = true,
        quotes = emptyList()
    )
}