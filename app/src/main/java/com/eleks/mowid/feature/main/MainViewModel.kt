package com.eleks.mowid.feature.main

import com.eleks.domain.intearactor.MotivationPhraseInteractor
import com.eleks.mowid.base.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val motivationPhraseInteractor: MotivationPhraseInteractor
) : BaseViewModel<MainState, MainEvent, MainEffect>() {

    override fun createInitialState(): MainState = MainState.Loading(state = false)

    override fun handleEvent(event: MainEvent) {
        //TODO:
    }

}