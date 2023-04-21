package com.eleks.mowid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.eleks.mowid.ui.feature.main.MainViewModel
import com.eleks.mowid.ui.feature.settings.SettingsScreen
import com.eleks.mowid.ui.feature.settings.SettingsViewModel

@Composable
fun SettingsScreenDestination(activityViewModel: MainViewModel, onBackClicked: () -> Unit) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    SettingsScreen(
        activityViewModel = activityViewModel,
        viewModel = settingsViewModel,
        onBackClicked = onBackClicked
    )
}
