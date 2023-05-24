package com.eleks.mowid.ui.feature.settings

import androidx.lifecycle.viewModelScope
import com.eleks.domain.interactor.MotivationPhraseInteractor
import com.eleks.domain.interactor.UserInteractor
import com.eleks.mowid.R
import com.eleks.mowid.base.ui.BaseViewModel
import com.eleks.mowid.model.toUIModel
import com.eleks.mowid.ui.worker.ExecutionOption
import com.eleks.mowid.ui.worker.QuotesWorkerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val quotesWorkerManager: QuotesWorkerManager,
    private val interactor: MotivationPhraseInteractor,
    private val userInteractor: UserInteractor
) : BaseViewModel<SettingsState, SettingsEvent, SettingsEffect>() {

    init {
        interactor.getFrequencySettingsFlow()
            .combine(userInteractor.getUserFlow()) { frequency, user ->
                frequency to user
            }
            .onStart {
                setState { copy(isLoading = true) }
            }
            .flowOn(Dispatchers.IO)
            .onEach { data ->
                setState {
                    copy(
                        isLoading = false,
                        selectedFrequency = data.first.selectedFrequency?.toUIModel(),
                        frequencies = data.first.frequencies.toUIModel().sortedBy { it.frequencyId },
                        userModel = data.second?.toUIModel()
                    )
                }
            }
            .onCompletion {
                setState { copy(isLoading = false) }
            }
            .catch {
                SettingsEffect.ShowToast(
                    message = it.message.toString()
                ).sendEffect()
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnFrequencyChanged -> {
                viewModelScope.launch {
                    interactor.updateUserFrequency(event.id)
                    quotesWorkerManager.execute(ExecutionOption.REGULAR)
                    SettingsEffect.ShowToastId(
                        messageId = R.string.label_applied
                    ).sendEffect()
                }
            }
            SettingsEvent.BackButtonClicked -> {}
        }
    }

    override fun createInitialState(): SettingsState = SettingsState(
        isLoading = true,
        selectedFrequency = null,
        frequencies = emptyList(),
        userModel = null,
    )
}
