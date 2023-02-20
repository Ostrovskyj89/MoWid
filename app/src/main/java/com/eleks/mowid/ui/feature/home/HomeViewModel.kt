package com.eleks.mowid.ui.feature.home

import androidx.compose.runtime.snapshotFlow
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

        snapshotFlow { }

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
            HomeEvent.ShowAddGroupModal -> {
                // handle directly on UI
            }
            HomeEvent.HideAddGroupModal -> {
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
        }
    }

}