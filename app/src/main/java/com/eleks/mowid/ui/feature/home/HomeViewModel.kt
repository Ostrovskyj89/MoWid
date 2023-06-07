package com.eleks.mowid.ui.feature.home

import androidx.lifecycle.viewModelScope
import com.eleks.domain.interactor.MotivationPhraseInteractor
import com.eleks.mowid.base.ui.BaseViewModel
import com.eleks.mowid.model.toUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val motivationPhraseInteractor: MotivationPhraseInteractor
) : BaseViewModel<HomeState, HomeEvent, HomeEffect>() {

    init {
        motivationPhraseInteractor.getGroupPhraseListFlow()
            .onStart {
                setState { copy(isLoading = true) }
            }
            .flowOn(Dispatchers.IO)
            .onEach { data ->
                setState {
                    copy(
                        isLoading = false,
                        groupPhraseList = data.toUIModel()
                    )
                }
            }
            .onCompletion {
                setState { copy(isLoading = false) }
            }
            .catch {
                HomeEffect.ShowError(
                    message = it.message.toString()
                ).sendEffect()
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            motivationPhraseInteractor.getRandomMeme()
                .catch {
                    HomeEffect.ShowError(
                        message = it.message.toString()
                    ).sendEffect()
                }
                .collect {
                    motivationPhraseInteractor.saveSelection(
                        groupId = "memesGroup",
                        quoteId = "memeId",
                        quote = null,
                        author = null,
                        memeUrl = it,
                        isSelected = true
                    )
                }
        }
    }

    override fun createInitialState(): HomeState = HomeState(
        isLoading = true,
        groupPhraseList = emptyList()
    )

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.GroupItemClicked -> {
                // handle directly on UI
            }

            HomeEvent.ShowGroupModal -> {
                // handle directly on UI
            }

            HomeEvent.HideGroupModal -> {
                // handle directly on UI
            }

            is HomeEvent.AddGroupClicked -> {
                viewModelScope.launch {
                    motivationPhraseInteractor.addGroup(
                        name = event.name,
                        description = event.description
                    )
                }
            }

            is HomeEvent.OnItemDeleted -> deleteGroup((event.id))
            is HomeEvent.OnEditClicked -> {
                viewModelScope.launch {
                    motivationPhraseInteractor.editGroup(
                        groupId = event.id,
                        editedName = event.editedName,
                        editedDescription = event.editedDescription
                    )
                }
            }

        }
    }

    private fun deleteGroup(id: String) {
        viewModelScope.launch {
            motivationPhraseInteractor.deleteGroup(id)
        }
    }
}