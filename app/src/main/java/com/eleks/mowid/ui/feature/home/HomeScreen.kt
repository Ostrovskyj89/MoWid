package com.eleks.mowid.ui.feature.home

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eleks.mowid.R
import com.eleks.mowid.base.ui.EFFECTS_KEY
import com.eleks.mowid.model.GroupPhraseUIModel
import com.eleks.mowid.ui.composable.Progress
import com.eleks.mowid.ui.feature.home.composable.HomeList
import com.eleks.mowid.ui.theme.MoWidTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(EFFECTS_KEY) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is HomeEffect.ShowError -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
                is HomeEffect.OpenGroup -> Toast.makeText(
                    context,
                    "TODO: Open group ${effect.groupPhrase.name}",
                    Toast.LENGTH_SHORT
                ).show()
                HomeEffect.AddGroup -> Toast.makeText(
                    context,
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
                    Text(text = stringResource(id = R.string.title_home))
                },
                colors = centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                actions = {
                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "TODO: description"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.isLoading.not()) FloatingActionButton(
                onClick = {
                    sendEvent(HomeEvent.AddGroupClicked)
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(Icons.Filled.Add, "TODO: description")
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

@Preview(showBackground = true)
@Composable
fun ScreenContentPreview() {
    MoWidTheme {
        val list = listOf(
            GroupPhraseUIModel(
                name = "Group 0",
                description = "Description 0",
                count = 10,
                selectedCount = 5
            ),
            GroupPhraseUIModel(
                name = "Group 1",
                description = "Description 1",
                count = 10,
                selectedCount = 5
            ),
            GroupPhraseUIModel(
                name = "Group 2",
                description = "Description 2",
                count = 10,
                selectedCount = 5
            )
        )

        ScreenContent(
            state = HomeState(
                isLoading = false,
                groupPhraseList = list
            ),
            sendEvent = {}
        )
    }
}