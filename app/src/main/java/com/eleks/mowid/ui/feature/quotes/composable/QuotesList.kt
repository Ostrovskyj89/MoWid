package com.eleks.mowid.ui.feature.quotes.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eleks.mowid.model.QuoteUIModel
import com.eleks.mowid.ui.theme.MoWidTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QuotesList(
    quotes: List<QuoteUIModel>,
    onCheckedChange: (String, Boolean) -> Unit,
    onItemDeleted: (String, Boolean) -> Unit,
    onEdit: (id: String, quote: String, author: String) -> Unit
) {
    LazyColumn {
        items(
            items = quotes,
            key = { quoteModel -> quoteModel.id }
        ) { item ->
            val currentItem by rememberUpdatedState(item)
            val dismissState = rememberDismissState(
                confirmValueChange = {
                    if (it == DismissValue.DismissedToStart) {
                        onItemDeleted(currentItem.id, currentItem.isSelected)
                    }
                    true
                },
                positionalThreshold = { 200.dp.toPx() }
            )
            if (item.canBeDeleted) {
                SwipeToDismiss(
                    modifier = Modifier.animateItemPlacement(),
                    state = dismissState,
                    background = { SwipeToDeleteBackground() },
                    dismissContent = {
                        QuoteListItem(
                            quote = item,
                            onCheckChanged = onCheckedChange,
                            onEdit = onEdit
                        )
                    },
                    directions = setOf(DismissDirection.EndToStart),
                )
            } else {
                QuoteListItem(
                    quote = item,
                    onCheckChanged = onCheckedChange,
                    onEdit = onEdit
                )
            }
            Divider(
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuotesListPreview() {
    MoWidTheme {
        QuotesList(
            listOf(
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
            ),
            onCheckedChange = { _, _ -> },
            onItemDeleted = { _, _ -> },
            onEdit = { _, _, _ -> }
        )
    }
}