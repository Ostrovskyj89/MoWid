package com.eleks.mowid.ui.feature.home.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eleks.mowid.R
import com.eleks.mowid.ui.theme.MoWidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    header: String,
    hint1: String,
    hint2: String,
    onAddClick: (String, String) -> Unit,
    clearSavedStates: Boolean = false
) {
    var groupTextState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var descriptionTextState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    if (clearSavedStates) {
        groupTextState = TextFieldValue()
        descriptionTextState = TextFieldValue()
        LocalFocusManager.current.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .size(width = 32.dp, height = 4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        Text(
            text = header,
            fontSize = 28.sp,
            style = MaterialTheme.typography.headlineMedium
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = groupTextState,
            onValueChange = { groupTextState = it },
            label = {
                Text(
                    text = hint1,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = descriptionTextState,
            onValueChange = { descriptionTextState = it },
            label = {
                Text(
                    text = hint2,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Button(
            modifier = Modifier.padding(16.dp),
            enabled = groupTextState.text.isNotEmpty() && descriptionTextState.text.isNotEmpty(),
            onClick = {
                onAddClick(groupTextState.text, descriptionTextState.text)
            }) {
            Text(
                text = stringResource(id = R.string.label_add),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBottomSheetPreview() {
    MoWidTheme {
        BottomSheet(
            header = stringResource(id = R.string.title_add_group),
            hint1 = stringResource(id = R.string.label_group),
            hint2 = stringResource(id = R.string.label_description),
            onAddClick = { _, _ ->

            }
        )
    }
}