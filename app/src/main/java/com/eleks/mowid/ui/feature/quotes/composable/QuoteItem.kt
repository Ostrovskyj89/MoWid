package com.eleks.mowid.ui.feature.quotes.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eleks.mowid.model.QuoteUIModel
import com.eleks.mowid.ui.theme.MoWidTheme

@Composable
fun QuoteListItem(
    quote: QuoteUIModel,
    onCheckChanged: (String, Boolean) -> Unit,
    onEdite: (id: String, quote: String, author: String) -> Unit
) {
    var checkedState by rememberSaveable { mutableStateOf(quote.isSelected) }

    QuoteListItem(
        quote = quote,
        checked = checkedState,
        onCheckChanged = { id, checked ->
            checkedState = checked
            onCheckChanged(id, checked)
        },
        onEdite = onEdite
    )
}

@Composable
fun QuoteListItem(
    quote: QuoteUIModel,
    checked: Boolean,
    onCheckChanged: (String, Boolean) -> Unit,
    onEdite: (id: String, quote: String, author: String) -> Unit
) {

    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .pointerInput(quote) {
                if (quote.canBeDeleted) {
                    detectTapGestures(
                        onLongPress = {
                            onEdite(quote.id, quote.quote, quote.author)
                        }
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
                .padding(start = 16.dp)
        ) {
            Text(
                text = quote.quote,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                modifier = Modifier.align(Alignment.End),
                text = quote.author,
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .padding(end = 16.dp)
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { isChecked ->
                    onCheckChanged(quote.id, isChecked)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuoteListItemPreview() {
    MoWidTheme {
        QuoteListItem(
            QuoteUIModel(
                id = "1",
                author = "Author",
                created = "",
                quote = "Quote",
                isSelected = true,
                canBeDeleted = true
            ),
            checked = true,
            onCheckChanged = { _, _ -> },
            onEdite = { _, _, _ -> }
        )
    }
}