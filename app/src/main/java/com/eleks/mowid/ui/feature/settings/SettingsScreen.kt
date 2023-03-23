package com.eleks.mowid.ui.feature.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eleks.data.firebase.source.impl.FirebaseDataSourceImpl
import com.eleks.mowid.R
import com.eleks.mowid.base.ui.EFFECTS_KEY
import com.eleks.mowid.base.ui.EVENTS_KEY
import com.eleks.mowid.model.FrequencyUIModel
import com.eleks.mowid.ui.composable.AppCenterAlignedTopAppBar
import com.eleks.mowid.ui.composable.AppProgress
import com.eleks.mowid.ui.theme.MoWidTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClicked: () -> Unit
) {
    val state: SettingsState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(EFFECTS_KEY) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is SettingsEffect.ShowToast -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
                is SettingsEffect.ShowToastId -> Toast.makeText(
                    context,
                    effect.messageId,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.collect()
    }

    LaunchedEffect(EVENTS_KEY) {
        viewModel.event.onEach { event ->
            when (event) {
                SettingsEvent.BackButtonClicked -> onBackClicked()
                is SettingsEvent.OnFrequencyChanged -> {}
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
fun ScreenContent(
    state: SettingsState,
    sendEvent: (SettingsEvent) -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppCenterAlignedTopAppBar(
                    title = stringResource(id = R.string.title_settings),
                    navigationIcon = {
                        IconButton(onClick = { sendEvent(SettingsEvent.BackButtonClicked) }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            when {
                state.isLoading -> AppProgress()
                else -> Content(padding, state, sendEvent)


            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Content(
    padding: PaddingValues,
    state: SettingsState,
    sendEvent: (SettingsEvent) -> Unit,
) {

    var showDropDown by remember { mutableStateOf(false) }

    var selectedFrequency by remember { mutableStateOf(state.selectedFrequency) }

    Surface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                expanded = showDropDown,
                onExpandedChange = { showDropDown = !showDropDown }) {

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = selectedFrequency?.value?.let { stringResource(id = it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.label_frequency)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "TODO"
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropDown)
                    }
                )

                ExposedDropdownMenu(
                    expanded = showDropDown,
                    onDismissRequest = { showDropDown = false }
                ) {
                    state.frequencies.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(stringResource(id = option.value)) },
                            onClick = {
                                selectedFrequency = option
                                showDropDown = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    sendEvent(
                        SettingsEvent.OnFrequencyChanged(
                            selectedFrequency?.frequencyId
                                ?: FirebaseDataSourceImpl.DEFAULT_FREQUENCY_VALUE
                        )
                    )
                }
            )
            {
                Text(
                    text = stringResource(id = R.string.label_apply),
                    style = MaterialTheme.typography.labelLarge
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
            FrequencyUIModel(
                frequencyId = 0,
                value = R.string.frequency_id_24,
            )
        )
        ScreenContent(
            state = SettingsState(
                isLoading = false,
                selectedFrequency = FrequencyUIModel(
                    frequencyId = 0,
                    value = R.string.frequency_id_24,
                ),
                frequencies = list,
            ),
            sendEvent = {}
        )
    }
}
