package com.eleks.mowid.ui.feature.home

import com.eleks.mowid.base.ui.UiEffect
import com.eleks.mowid.base.ui.UiEvent
import com.eleks.mowid.base.ui.UiState
import com.eleks.mowid.model.GroupPhraseUIModel

data class HomeState(
    val isLoading: Boolean,
    val groupPhraseList: List<GroupPhraseUIModel>
) : UiState

sealed class HomeEvent : UiEvent {
    data class GroupItemClicked(val groupPhrase: GroupPhraseUIModel) : HomeEvent()
    object ShowAddGroupModal : HomeEvent()
    object HideAddGroupModal : HomeEvent()
    data class AddGroupClicked(val name: String, val description: String) : HomeEvent()
}

sealed class HomeEffect : UiEffect {
    data class ShowError(val message: String) : HomeEffect()
}