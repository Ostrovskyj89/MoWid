package com.eleks.mowid.ui.feature.main

import com.eleks.mowid.base.ui.UiEffect
import com.eleks.mowid.base.ui.UiEvent
import com.eleks.mowid.base.ui.UiState

sealed class MainState : UiState {
    data class Loading(val state: Boolean) : MainState()
}

sealed class MainEvent : UiEvent {
    object SignIn : MainEvent()
    object SignOut : MainEvent()
}

sealed class MainEffect : UiEffect {
    object SignIn : MainEffect()
    object SignOut : MainEffect()
}