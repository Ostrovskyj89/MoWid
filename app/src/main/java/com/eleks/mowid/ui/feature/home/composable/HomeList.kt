package com.eleks.mowid.ui.feature.home.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.eleks.mowid.model.GroupPhraseUIModel
import com.eleks.mowid.ui.theme.MoWidTheme

@Composable
fun HomeList(
    groupPhraseList: List<GroupPhraseUIModel>,
    onClick: (groupPhrase: GroupPhraseUIModel) -> Unit
) {
    LazyColumn {
        items(items = groupPhraseList) { item ->
            HomeListItem(
                groupPhrase = item,
                onClick = {
                    onClick(it)
                }
            )
            Divider(
                color = MaterialTheme.colorScheme.outlineVariant,

                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeListPreview() {
    MoWidTheme {
        HomeList(
            listOf(
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
            ),
            onClick = {}
        )
    }
}