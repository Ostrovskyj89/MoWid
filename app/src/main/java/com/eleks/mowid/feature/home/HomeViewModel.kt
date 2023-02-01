package com.eleks.mowid.feature.home

import com.eleks.domain.intearactor.MotivationPhraseInteractor
import com.eleks.mowid.base.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val motivationPhraseInteractor: MotivationPhraseInteractor
) : BaseViewModel<HomeState, HomeEvent, HomeEffect>() {

    override fun createInitialState(): HomeState = HomeState.Loading(state = true)

    override fun handleEvent(event: HomeEvent) {
        //TODO:
    }

}