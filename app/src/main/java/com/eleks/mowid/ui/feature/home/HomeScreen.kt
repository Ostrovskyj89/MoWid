package com.eleks.mowid.ui.feature.home

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eleks.mowid.base.ui.EFFECTS_KEY
import com.eleks.mowid.ui.composable.Progress
import com.eleks.mowid.ui.feature.home.composable.HomeList
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()
    val mContext = LocalContext.current

    LaunchedEffect(EFFECTS_KEY) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is HomeEffect.ShowError -> Toast.makeText(
                    mContext,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.collect()
    }

    when {
        state.isLoading -> Progress()
        else -> HomeList(groupPhraseList = state.groupPhraseList)
    }
}