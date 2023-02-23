package com.eleks.mowid.ui.feature.main

import androidx.core.app.ComponentActivity
import com.eleks.domain.intearactor.UserInteractor
import com.eleks.mowid.R
import com.eleks.mowid.base.ui.BaseViewModel
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val interactor: UserInteractor
) : BaseViewModel<MainState, MainEvent, MainEffect>() {

    override fun createInitialState(): MainState = MainState.Loading(state = false)

    override fun handleEvent(event: MainEvent) {}

    fun isUserAlreadyLogIn() = interactor.getCurrentUser() != null

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            interactor.signInSuccess()
            MainEffect.ShowToast(R.string.label_sign_in_success).sendEffect()
        } else {
            MainEffect.ShowToast(R.string.label_sign_in_error).sendEffect()
        }
    }

    fun signOutSuccess() {
        interactor.signOutSuccess()
    }
}