package com.eleks.mowid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.eleks.mowid.ui.feature.settings.SettingsScreen
import com.eleks.mowid.ui.feature.settings.SettingsViewModel

@Composable
fun SettingsScreenDestination(onBackClicked: () -> Unit) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    SettingsScreen(
        viewModel = settingsViewModel,
        onBackClicked
    )
}
