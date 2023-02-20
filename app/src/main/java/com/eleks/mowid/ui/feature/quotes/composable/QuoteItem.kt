package com.eleks.mowid.ui.feature.quotes.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eleks.mowid.model.QuoteUIModel
import com.eleks.mowid.ui.theme.MoWidTheme

@Composable
fun QuoteListItem(
    quote: QuoteUIModel,
    onCheckChanged: (String, Boolean) -> Unit
) {
    var checkedState by rememberSaveable { mutableStateOf(quote.isSelected) }

    QuoteListItem(
        quote = quote,
        checked = checkedState,
        onCheckChanged = { id, checked ->
            checkedState = checked
            onCheckChanged(id, checked)
        }
    )
}

@Composable
fun QuoteListItem(
    quote: QuoteUIModel,
    checked: Boolean,
    onCheckChanged: (String, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(64.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
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
                isSelected = true
            ),
            checked = true,
            onCheckChanged = { _, _ -> }
        )
    }
}