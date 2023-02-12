package com.eleks.mowid.ui.feature.home.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eleks.mowid.model.GroupPhraseUIModel
import com.eleks.mowid.ui.theme.MoWidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeListItem(
    groupPhrase: GroupPhraseUIModel,
    onClick: (groupPhrase: GroupPhraseUIModel) -> Unit
) {
    Surface(onClick = { onClick(groupPhrase) }) {
        Row(modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(64.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = groupPhrase.name,
                    fontSize = 16.sp
                )
                Text(
                    text = groupPhrase.description,
                    fontSize = 14.sp
                )
            }
            Column {
                Text(
                    text = "${groupPhrase.selectedCount}/${groupPhrase.count}",
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeListItemPreview() {
    MoWidTheme {
        HomeListItem(
            GroupPhraseUIModel(
                name = "Group 0",
                description = "Description 0",
                count = 10,
                selectedCount = 5
            ),
            onClick = {}
        )
    }
}