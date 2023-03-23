package com.eleks.mowid.ui.feature.settings

import androidx.lifecycle.viewModelScope
import com.eleks.data.firebase.source.impl.FirebaseDataSourceImpl
import com.eleks.domain.intearactor.MotivationPhraseInteractor
import com.eleks.mowid.R
import com.eleks.mowid.base.ui.BaseViewModel
import com.eleks.mowid.model.FrequencyUIModel
import com.eleks.mowid.model.toUIModel
import com.eleks.mowid.ui.worker.ExecutionOption
import com.eleks.mowid.ui.worker.QuotesWorkerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val quotesWorkerManager: QuotesWorkerManager,
    private val interactor: MotivationPhraseInteractor,
) : BaseViewModel<SettingsState, SettingsEvent, SettingsEffect>() {

    init {
        interactor.getFrequencySettingsFlow()
            .onStart {
                setState { copy(isLoading = true) }
            }
            .flowOn(Dispatchers.IO)
            .onEach { data ->
                setState {
                    copy(
                        isLoading = false,
                        selectedFrequency = data.selectedFrequency?.toUIModel(),
                        frequencies = data.settings.toUIModel().sortedBy { it.frequencyId }
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
        selectedFrequency = FrequencyUIModel(FirebaseDataSourceImpl.DEFAULT_FREQUENCY_VALUE, ""),
        frequencies = emptyList()
    )
}
