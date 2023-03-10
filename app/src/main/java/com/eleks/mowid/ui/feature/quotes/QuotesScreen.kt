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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eleks.mowid.R
import com.eleks.mowid.base.ui.EFFECTS_KEY
import com.eleks.mowid.base.ui.EVENTS_KEY
import com.eleks.mowid.model.QuoteUIModel
import com.eleks.mowid.ui.composable.AppCenterAlignedTopAppBar
import com.eleks.mowid.ui.composable.AppFloatingActionButton
import com.eleks.mowid.ui.composable.AppProgress
import com.eleks.mowid.ui.composable.bottomsheet.BottomSheetScaffold
import com.eleks.mowid.ui.composable.bottomsheet.BottomSheetScaffoldState
import com.eleks.mowid.ui.composable.bottomsheet.rememberBottomSheetScaffoldState
import com.eleks.mowid.ui.feature.home.composable.BottomSheet
import com.eleks.mowid.ui.feature.quotes.composable.QuotesList
import com.eleks.mowid.ui.theme.MoWidTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@Composable
fun QuotesScreen(viewModel: QuotesViewModel, groupName: String) {
    val state: QuotesState by viewModel.uiState.collectAsStateWithLifecycle()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current

    BackHandler(
        enabled = bottomSheetScaffoldState.bottomSheetState.isExpanded
    ) {
        if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
            viewModel.setEvent(QuotesEvent.HideAddQuoteModal)
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
                QuotesEvent.ShowAddQuoteModal -> {
                    if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    } else {
                        bottomSheetScaffoldState.bottomSheetState.collapse()
                    }
                }
                is QuotesEvent.QuoteItemChecked -> Toast.makeText(
                    context,
                    "item checked, unchecked",
                    Toast.LENGTH_SHORT
                ).show()
                QuotesEvent.HideAddQuoteModal -> {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
                is QuotesEvent.AddQuoteClicked -> {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }
        }.collect()
    }

    ScreenContent(
        groupName = groupName,
        state = state,
        sendEvent = viewModel::setEvent,
        bottomSheetState = bottomSheetScaffoldState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    groupName: String,
    state: QuotesState,
    sendEvent: (QuotesEvent) -> Unit,
    bottomSheetState: BottomSheetScaffoldState
) {
    BottomSheetScaffold(
        sheetContent = {
            BottomSheet(
                header = stringResource(id = R.string.title_add_quote),
                hint1 = stringResource(id = R.string.label_quote),
                hint2 = stringResource(id = R.string.label_author),
                isSecondFieldOptional = true,
                onAddClick = { quote, author ->
                    sendEvent(
                        QuotesEvent.AddQuoteClicked(
                            quote = quote,
                            author = author
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
                        title = groupName,
                        actions = {
                            IconButton(onClick = {
                                //TODO:
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
                    if (state.isLoading.not()) AppFloatingActionButton(
                        onClick = {
                            sendEvent(QuotesEvent.ShowAddQuoteModal)
                        }
                    ) else Unit
                }
            ) { padding ->
                Column(
                    modifier = Modifier.padding(padding)
                ) {
                    when {
                        state.isLoading -> AppProgress()
                        else -> QuotesList(
                            quotes = state.quotes,
                            onCheckedChange = { id, checked ->
                                sendEvent(QuotesEvent.QuoteItemChecked(id, checked))
                            }
                        )
                    }
                }
            }
            if (bottomSheetState.bottomSheetState.isExpanded) {
                Box(
                    modifier = Modifier
                        .clickable {
                            sendEvent(QuotesEvent.HideAddQuoteModal)
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
                isSelected = true
            ),
            QuoteUIModel(
                id = "2",
                author = "Author 2 ",
                created = "",
                quote = "Quote 2 ",
                isSelected = true
            ),
            QuoteUIModel(
                id = "3",
                author = "Author 3 ",
                created = "",
                quote = "Quote 3 ",
                isSelected = true
            )
        )

        ScreenContent(
            groupName = "Group 1",
            state = QuotesState(
                isLoading = false,
                quotes = list
            ),
            sendEvent = {},
            bottomSheetState = rememberBottomSheetScaffoldState()
        )
    }
}