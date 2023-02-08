package com.eleks.mowid.ui.composable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.eleks.mowid.ui.theme.MoWidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCenterAlignedTopAppBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        actions = actions
    )
}

@Preview(showBackground = true)
@Composable
fun AppCenterAlignedTopAppBarPreview() {
    MoWidTheme {
        AppCenterAlignedTopAppBar(
            title = "Title"
        )
    }
}