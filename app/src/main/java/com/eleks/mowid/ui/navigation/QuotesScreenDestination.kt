package com.eleks.mowid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.eleks.mowid.ui.feature.main.MainViewModel
import com.eleks.mowid.ui.feature.quotes.QuotesScreen
import com.eleks.mowid.ui.feature.quotes.QuotesViewModel

@Composable
fun QuotesScreenDestination(activityViewModel: MainViewModel, groupName: String, onBackClicked: () -> Unit) {
    val viewModel: QuotesViewModel = hiltViewModel()
    QuotesScreen(viewModel, activityViewModel, groupName, onBackClicked)
}
