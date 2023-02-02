package com.eleks.mowid.ui.feature.home

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
                is HomeEffect.OpenGroup -> Toast.makeText(
                    mContext,
                    "TODO: Open group ${effect.groupPhrase.name}",
                    Toast.LENGTH_SHORT
                ).show()
                HomeEffect.AddGroup -> Toast.makeText(
                    mContext,
                    "TODO: Add new group",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.collect()
    }
    ScreenContent(
        state = state,
        sendEvent = viewModel::setEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(state: HomeState, sendEvent: (HomeEvent) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Motivation widget")
                }
            )
        },
        floatingActionButton = {
            if (state.isLoading.not()) FloatingActionButton(
                onClick = {
                    sendEvent(HomeEvent.AddGroupClicked)
                }
            ) {
                Icon(Icons.Filled.Add, "")
            }
            else Unit
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            when {
                state.isLoading -> Progress()
                else -> HomeList(
                    groupPhraseList = state.groupPhraseList,
                    onClick = {
                        sendEvent(HomeEvent.GroupItemClicked(it))
                    }
                )
            }
        }

    }
}