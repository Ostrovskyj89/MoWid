package com.eleks.mowid.feature.home

import com.eleks.mowid.base.ui.UiEffect
import com.eleks.mowid.base.ui.UiEvent
import com.eleks.mowid.base.ui.UiState

sealed class HomeState : UiState {
    data class Loading(val state: Boolean) : HomeState()
    sealed class View1State: HomeState() {
        object Loading : View1State()
        object Empty : View1State()
    }
    sealed class View2State: HomeState()  {
        object Loading : View2State()
        object Empty : View2State()
    }
}

sealed class HomeEvent : UiEvent {

}

sealed class HomeEffect : UiEffect {

}