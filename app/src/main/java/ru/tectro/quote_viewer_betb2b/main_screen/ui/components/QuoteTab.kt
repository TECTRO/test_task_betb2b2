package ru.tectro.quote_viewer_betb2b.main_screen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.tectro.quote_viewer_betb2b.domain.entities.Quote

@Composable
fun QuoteTab(quote: Quote, onButtonClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(text = quote.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = quote.isoCharCode)
        }

        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = quote.value.toString()
        )

        IconToggleButton(checked = quote.isFavourite, onCheckedChange = { onButtonClick() }) {
            if (quote.isFavourite)
                Icon(imageVector = Icons.Rounded.Clear, contentDescription = null)
            else
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
        }
    }
}