package com.eleks.mowid.ui.feature.settings

import androidx.annotation.StringRes
import com.eleks.mowid.base.ui.UiEffect
import com.eleks.mowid.base.ui.UiEvent
import com.eleks.mowid.base.ui.UiState
import com.eleks.mowid.model.FrequencyUIModel
import com.eleks.mowid.model.UserUIModel

data class SettingsState(
    val isLoading: Boolean,
    val selectedFrequency: FrequencyUIModel?,
    val frequencies: List<FrequencyUIModel>,
    val userModel: UserUIModel?
) : UiState

sealed class SettingsEvent : UiEvent {
    data class OnFrequencyChanged(val id: Long) : SettingsEvent()
    object BackButtonClicked : SettingsEvent()
}

sealed class SettingsEffect : UiEffect {
    data class ShowToast(val message: String) : SettingsEffect()
    data class ShowToastId(@StringRes val messageId: Int) : SettingsEffect()
}
