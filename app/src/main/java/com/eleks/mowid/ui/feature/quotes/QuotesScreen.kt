package com.eleks.mowid.ui.feature.quotes

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eleks.mowid.base.ui.EFFECTS_KEY
import com.eleks.mowid.base.ui.EVENTS_KEY
import com.eleks.mowid.model.QuoteUIModel
import com.eleks.mowid.ui.composable.*
import com.eleks.mowid.ui.composable.bottomsheet.BottomSheetScaffold
import com.eleks.mowid.ui.composable.bottomsheet.BottomSheetScaffoldState
import com.eleks.mowid.ui.composable.bottomsheet.rememberBottomSheetScaffoldState
import com.eleks.mowid.ui.feature.bottomsheet.BottomSheet
import com.eleks.mowid.ui.feature.bottomsheet.BottomSheetUIState
import com.eleks.mowid.ui.feature.main.MainEvent
import com.eleks.mowid.ui.feature.main.MainViewModel
import com.eleks.mowid.ui.feature.quotes.composable.EmptyState
import com.eleks.mowid.ui.feature.quotes.composable.QuotesList
import com.eleks.mowid.ui.theme.MoWidTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@Composable
fun QuotesScreen(
    viewModel: QuotesViewModel,
    activityViewModel: MainViewModel,
    groupName: String,
    onBackClicked: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state: QuotesState by viewModel.uiState.collectAsStateWithLifecycle()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current

    BackHandler(
        enabled = bottomSheetScaffoldState.bottomSheetState.isExpanded
    ) {
        if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
            viewModel.setEvent(QuotesEvent.HideQuoteModal)
        }
    }

    LaunchedEffect(EFFECTS_KEY) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is QuotesEffect.ShowError -> Toast.makeText(
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
                QuotesEvent.ShowQuoteModal -> {
                    if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    } else {
                        bottomSheetScaffoldState.bottomSheetState.collapse()
                    }
                }
                is QuotesEvent.QuoteItemChecked -> {}
                QuotesEvent.HideQuoteModal -> {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
                is QuotesEvent.AddQuoteClicked -> {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
                QuotesEvent.BackButtonClicked -> onBackClicked()
                is QuotesEvent.OnItemDeleted -> {}
                is QuotesEvent.OnEditeClicked -> {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }
        }.collect()
    }

    ScreenContent(
        groupName = groupName,
        state = state,
        sendEvent = viewModel::setEvent,
        sendMainEvent = activityViewModel::setEvent,
        isUserAlreadyLogin = activityViewModel::isUserAlreadyLogIn,
        bottomSheetState = bottomSheetScaffoldState,
        onNavigateToSettings = onNavigateToSettings
    )
}

@Composable
fun ScreenContent(
    groupName: String,
    state: QuotesState,
    sendEvent: (QuotesEvent) -> Unit,
    sendMainEvent: (MainEvent) -> Unit,
    isUserAlreadyLogin: () -> Boolean,
    bottomSheetState: BottomSheetScaffoldState,
    onNavigateToSettings: () -> Unit
) {

    var showMenu by remember { mutableStateOf(false) }
    var bottomSheetUIState: BottomSheetUIState by remember { mutableStateOf(BottomSheetUIState.AddQuoteBottomSheet) }

    BottomSheetScaffold(
        sheetContent = {
            BottomSheet(
                bottomSheetUIState = bottomSheetUIState,
                onButtonClick = { id, quote, author ->
                    if (id != null) {
                        sendEvent(
                            QuotesEvent.OnEditeClicked(
                                id = id,
                                editedQuote = quote,
                                editedAuthor = author
                            )
                        )
                    } else {
                        sendEvent(
                            QuotesEvent.AddQuoteClicked(
                                quote = quote,
                                author = author
                            )
                        )
                    }
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
                        title = groupName,
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
                        },

                        navigationIcon = {
                            IconButton(onClick = { sendEvent(QuotesEvent.BackButtonClicked) }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    if (state.isLoading.not() && state.quotes.isNotEmpty()) AppFloatingActionButton(
                        onClick = {
                            bottomSheetUIState = BottomSheetUIState.AddQuoteBottomSheet
                            sendEvent(QuotesEvent.ShowQuoteModal)
                        }
                    ) else Unit
                }
            ) { padding ->
                Column(
                    modifier = Modifier.padding(padding)
                ) {
                    when {
                        state.isLoading -> AppProgress()
                        state.quotes.isEmpty() -> EmptyState {
                            sendEvent(QuotesEvent.ShowQuoteModal)
                        }
                        else -> QuotesList(
                            quotes = state.quotes,
                            onCheckedChange = { id, checked ->
                                val quote = state.quotes.firstOrNull { it.id == id }
                                sendEvent(
                                    QuotesEvent.QuoteItemChecked(
                                        quoteId = id,
                                        quote = quote?.quote ?: "",
                                        author = quote?.author,
                                        checked = checked
                                    )
                                )
                            },
                            onItemDeleted = { id, isSelected ->
                                sendEvent(QuotesEvent.OnItemDeleted(id, isSelected))
                            },
                            onEdite = { id, editedQuote, editedAuthor ->
                                bottomSheetUIState = BottomSheetUIState.EditQuoteBottomSheet(
                                    id = id,
                                    text1 = editedQuote,
                                    text2 = editedAuthor
                                )
                                sendEvent(QuotesEvent.ShowQuoteModal)
                            }
                        )
                    }
                }
            }
            if (bottomSheetState.bottomSheetState.isExpanded) {
                Box(
                    modifier = Modifier
                        .clickable {
                            sendEvent(QuotesEvent.HideQuoteModal)
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
            QuoteUIModel(
                id = "1",
                author = "Author 1 ",
                created = "",
                quote = "Quote 1 ",
                isSelected = true,
                canBeDeleted = true,
            ),
            QuoteUIModel(
                id = "2",
                author = "Author 2 ",
                created = "",
                quote = "Quote 2 ",
                isSelected = true,
                canBeDeleted = true,
            ),
            QuoteUIModel(
                id = "3",
                author = "Author 3 ",
                created = "",
                quote = "Quote 3 ",
                isSelected = true,
                canBeDeleted = true,
            )
        )

        ScreenContent(
            groupName = "Group 1",
            state = QuotesState(
                isLoading = false,
                quotes = list
            ),
            sendEvent = {},
            sendMainEvent = {},
            isUserAlreadyLogin = { false },
            bottomSheetState = rememberBottomSheetScaffoldState(),
            onNavigateToSettings = {}
        )
    }
}