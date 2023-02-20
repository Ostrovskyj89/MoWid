package com.eleks.mowid.ui.feature.quotes.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.eleks.mowid.model.QuoteUIModel
import com.eleks.mowid.ui.theme.MoWidTheme

@Composable
fun QuotesList(
    quotes: List<QuoteUIModel>,
    onCheckedChange: (String, Boolean) -> Unit
) {
    LazyColumn {
        items(items = quotes) { item ->
            QuoteListItem(
                quote = item,
                onCheckChanged = onCheckedChange
            )
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
            ),
            onCheckedChange = { _, _ -> }
        )
    }
}