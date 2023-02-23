package com.eleks.mowid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.eleks.mowid.ui.feature.home.HomeScreen
import com.eleks.mowid.ui.feature.home.HomeViewModel

@Composable
fun HomeScreenDestination(activityViewModel: MainViewModel, onNavigateToQuotes: (String, String) -> Unit) {
    val viewModel: HomeViewModel = hiltViewModel()
    HomeScreen(
        viewModel = viewModel,
        activityViewModel = activityViewModel,
        onNavigateToQuotes = onNavigateToQuotes
    )
}