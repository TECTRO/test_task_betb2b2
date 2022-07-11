package ru.tectro.quote_viewer_betb2b.main_screen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ru.tectro.quote_viewer_betb2b.main_screen.ui.components.QuoteTab
import ru.tectro.quote_viewer_betb2b.main_screen.viewmodel.QuotesEvents
import ru.tectro.quote_viewer_betb2b.main_screen.viewmodel.QuotesViewModel

@Composable
fun MainScreen(viewModel: QuotesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
        if (!state.isQuotesLoading && state.quotesHeader == null)
            viewModel.onEvent(QuotesEvents.LoadLatestQuotes)
    })

    Scaffold { padding ->
        Column(Modifier.padding(padding)) {

            state.quotesHeader?.let {
                Text(text = "Источник: ${it.owner}")
                Text(text = "Котировки от ${it.date.toString("dd.MM.yyyy")}")
            }
            LazyColumn {
                items(state.quotesHeader?.filteredQuotes.orEmpty()) { quote ->
                    QuoteTab(quote = quote) {
                        viewModel.onEvent(
                            if (quote.isFavourite)
                                QuotesEvents.RemoveFromFavorites(quote)
                            else
                                QuotesEvents.AddToFavorites(quote)
                        )
                    }
                }

                if (state.isQuotesLoading)
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
            }
        }
    }
}