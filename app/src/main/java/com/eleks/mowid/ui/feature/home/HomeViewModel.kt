package com.eleks.mowid.ui.feature.home

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.eleks.domain.intearactor.MotivationPhraseInteractor
import com.eleks.mowid.base.ui.BaseViewModel
import com.eleks.mowid.model.toUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
                    message = this.toString()
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
            is HomeEvent.GroupItemClicked -> HomeEffect.OpenGroup(event.groupPhrase).sendEffect()
            HomeEvent.AddGroupClicked -> HomeEffect.AddGroup.sendEffect()
        }
    }

}