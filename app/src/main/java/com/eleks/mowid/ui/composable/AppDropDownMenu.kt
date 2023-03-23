package com.eleks.mowid.ui.composable

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eleks.mowid.R
import com.eleks.mowid.ui.feature.main.MainEvent

@Composable
fun AppDropDownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    sendEvent: (MainEvent) -> Unit,
    onSettingsClicked: () -> Unit,
    isUserLogIn: Boolean
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = {
                Text(
                    text = if (isUserLogIn) {
                        stringResource(id = R.string.label_sign_out)
                    } else {
                        stringResource(id = R.string.label_sign_in)
                    }
                )
            },
            onClick = {
                val event = if (isUserLogIn) {
                    MainEvent.SignOut
                } else {
                    MainEvent.SignIn
                }
                sendEvent(event)
                onDismissRequest()
            })

        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.title_settings)) },
            onClick = {
                onSettingsClicked()
                onDismissRequest()
            })
    }
}
