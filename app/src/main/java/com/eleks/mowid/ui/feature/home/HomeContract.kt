package com.eleks.mowid.ui.feature.home

import com.eleks.mowid.base.ui.UiEffect
import com.eleks.mowid.base.ui.UiEvent
import com.eleks.mowid.base.ui.UiState
import com.eleks.mowid.model.GroupPhraseUIModel

data class HomeState(
    val isLoading: Boolean,
    val groupPhraseList: List<GroupPhraseUIModel>
) : UiState

sealed class HomeEvent : UiEvent

sealed class HomeEffect : UiEffect {
    data class ShowError(val message: String) : HomeEffect()
}