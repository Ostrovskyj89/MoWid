package com.eleks.mowid.feature

import androidx.lifecycle.ViewModel
import com.eleks.domain.intearactor.MotivationPhraseInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val motivationPhraseInteractor: MotivationPhraseInteractor
) : ViewModel() {

}