package com.eleks.mowid.ui.feature.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eleks.mowid.R
import com.eleks.mowid.base.ui.EFFECTS_KEY
import com.eleks.mowid.base.ui.EVENTS_KEY
import com.eleks.mowid.model.GroupPhraseUIModel
import com.eleks.mowid.ui.composable.*
import com.eleks.mowid.ui.composable.bottomsheet.BottomSheetScaffold
import com.eleks.mowid.ui.composable.bottomsheet.BottomSheetScaffoldState
import com.eleks.mowid.ui.composable.bottomsheet.rememberBottomSheetScaffoldState
import com.eleks.mowid.ui.feature.home.composable.BottomSheet
import com.eleks.mowid.ui.feature.home.composable.HomeList
import com.eleks.mowid.ui.feature.main.MainEvent
import com.eleks.mowid.ui.feature.main.MainViewModel
import com.eleks.mowid.ui.theme.MoWidTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    activityViewModel: MainViewModel,
    onNavigateToQuotes: (String, String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state: HomeState by viewModel.uiState.collectAsStateWithLifecycle()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current

    BackHandler(
        enabled = bottomSheetScaffoldState.bottomSheetState.isExpanded
    ) {
        if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
            viewModel.setEvent(HomeEvent.HideAddGroupModal)
        }
    }

    LaunchedEffect(EFFECTS_KEY) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is HomeEffect.ShowError -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.collect()
    }

    LaunchedEffect(EVENTS_KEY) {
        viewModel.event.onEach { event ->
            when (event) {
                HomeEvent.ShowAddGroupModal -> {
                    if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    } else {
                        bottomSheetScaffoldState.bottomSheetState.collapse()
                    }
                }
                is HomeEvent.GroupItemClicked -> onNavigateToQuotes(
                    event.groupPhrase.id,
                    event.groupPhrase.name
                )
                HomeEvent.HideAddGroupModal -> {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
                is HomeEvent.AddGroupClicked -> {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }
        }.collect()
    }


    ScreenContent(
        state = state,
        sendEvent = viewModel::setEvent,
        sendMainEvent = activityViewModel::setEvent,
        isUserAlreadyLogin = activityViewModel::isUserAlreadyLogIn,
        bottomSheetState = bottomSheetScaffoldState,
        onNavigateToSettings = onNavigateToSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    state: HomeState,
    sendEvent: (HomeEvent) -> Unit,
    sendMainEvent: (MainEvent) -> Unit,
    isUserAlreadyLogin: () -> Boolean,
    bottomSheetState: BottomSheetScaffoldState,
    onNavigateToSettings: () -> Unit
) {

    var showMenu by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        sheetContent = {
            BottomSheet(
                header = stringResource(id = R.string.title_add_group),
                hint1 = stringResource(id = R.string.label_group),
                hint2 = stringResource(id = R.string.label_description),
                onAddClick = { group, author ->
                    sendEvent(
                        HomeEvent.AddGroupClicked(
                            name = group,
                            description = author
                        )
                    )
                },
                clearSavedStates = bottomSheetState.bottomSheetState.isCollapsed
            )
        },
        scaffoldState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        sheetPeekHeight = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    AppCenterAlignedTopAppBar(
                        title = stringResource(id = R.string.title_home),
                        actions = {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "TODO: description"
                                )
                            }
                            AppDropDownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                sendEvent = sendMainEvent,
                                isUserLogIn = isUserAlreadyLogin(),
                                onSettingsClicked = onNavigateToSettings
                            )
                        }
                    )
                },
                floatingActionButton = {
                    if (state.isLoading.not()) AppFloatingActionButton(
                        onClick = {
                            sendEvent(HomeEvent.ShowAddGroupModal)
                        }
                    ) else Unit
                }
            ) { padding ->
                Column(
                    modifier = Modifier.padding(padding)
                ) {
                    when {
                        state.isLoading -> AppProgress()
                        else -> HomeList(
                            groupPhraseList = state.groupPhraseList,
                            onClick = {
                                sendEvent(HomeEvent.GroupItemClicked(it))
                            }
                        )
                    }
                }
            }
            if (bottomSheetState.bottomSheetState.isExpanded) {
                Box(
                    modifier = Modifier
                        .clickable {
                            sendEvent(HomeEvent.HideAddGroupModal)
                        }
                        .background(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.20F))
                        .fillMaxSize(),
                ) {

                }
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
                id = "1",
                name = "Group 0",
                description = "Description 0",
                count = 10,
                selectedCount = 5
            ),
            GroupPhraseUIModel(
                id = "2",
                name = "Group 1",
                description = "Description 1",
                count = 10,
                selectedCount = 5
            ),
            GroupPhraseUIModel(
                id = "3",
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
            sendEvent = {},
            sendMainEvent = {},
            isUserAlreadyLogin = { false },
            bottomSheetState = rememberBottomSheetScaffoldState(),
            onNavigateToSettings = {}
        )
    }
}