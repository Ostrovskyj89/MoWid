package com.eleks.mowid.ui.feature.bottomsheet

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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eleks.mowid.ui.theme.MoWidTheme

@Composable
fun BottomSheet(
    bottomSheetUIState: BottomSheetUIState,
    onButtonClick: (id: String?, text1: String, text2: String) -> Unit,
    clearSavedStates: Boolean = false
) {


    with(bottomSheetUIState) {
        var textState1 by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(textField1))
        }
        var textState2 by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(textField2))
        }

        if (clearSavedStates) {
            textState1 = TextFieldValue(textField1)
            textState2 = TextFieldValue(textField2)
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
                text = stringResource(header),
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = textState1,
                onValueChange = { textState1 = it },
                label = {
                    Text(
                        text = stringResource(hint1),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = textState2,
                onValueChange = { textState2 = it },
                label = {
                    Text(
                        text = stringResource(hint2),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences
                )
            )

            Button(
                modifier = Modifier.padding(16.dp),
                enabled = textState1.text.isNotEmpty() && (textState2.text.isNotEmpty() || !isSecondFieldMandatory),
                onClick = {
                    onButtonClick(
                        id,
                        textState1.text,
                        textState2.text
                    )
                }) {
                Text(
                    text = stringResource(buttonLabel),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBottomSheetPreview() {
    MoWidTheme {
        BottomSheet(
            bottomSheetUIState = BottomSheetUIState.AddGroupBottomSheet,
            onButtonClick = { _, _, _ -> }
        )
    }
}