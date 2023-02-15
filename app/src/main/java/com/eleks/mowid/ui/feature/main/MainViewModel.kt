package com.eleks.mowid.ui.feature.main

import android.util.Log
import androidx.activity.ComponentActivity
import com.eleks.domain.intearactor.MotivationPhraseInteractor
import com.eleks.mowid.base.ui.BaseViewModel
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val interactor: MotivationPhraseInteractor
) : BaseViewModel<MainState, MainEvent, MainEffect>() {

    override fun createInitialState(): MainState = MainState.Loading(state = false)

    override fun handleEvent(event: MainEvent) {
        when (event) {
            MainEvent.SignIn -> MainEffect.SignIn.sendEffect()
            MainEvent.SignOut -> MainEffect.SignOut.sendEffect()
        }
    }

    fun isUserAlreadyLogIn() = interactor.getCurrentUser() != null
}