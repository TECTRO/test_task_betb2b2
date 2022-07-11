package ru.tectro.quote_viewer_betb2b.main_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.repo.IQuotesRepository
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.FlowResponse
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.ResponseError
import ru.tectro.quote_viewer_betb2b.domain.entities.Quote
import ru.tectro.quote_viewer_betb2b.domain.entities.QuotesHeader
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedField
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedOrder
import javax.inject.Inject

data class QuotesState(
    val quotesHeader: QuotesHeader? = null,
    val isQuotesLoading: Boolean = false,
    val quotesLoadingError: ResponseError? = null,

    val sortedField: SortedField = SortedField.IsFavourite,
    val sortedOrder: SortedOrder = SortedOrder.DESC
)

sealed class QuotesEvents {
    object LoadLatestQuotes : QuotesEvents()
    data class LoadQuotesByDate(val date: LocalDate) : QuotesEvents()

    data class AddToFavorites(val quote: Quote) : QuotesEvents()
    data class RemoveFromFavorites(val quote: Quote) : QuotesEvents()

    data class SetSortedRules(val sortedOrder: SortedOrder, val sortedField: SortedField) :
        QuotesEvents()
}

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val repo: IQuotesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(QuotesState())
    val state = _state.asStateFlow()

    fun onEvent(event: QuotesEvents) {
        when (event) {
            QuotesEvents.LoadLatestQuotes -> viewModelScope.launch { onLoadQuotes() }
            is QuotesEvents.LoadQuotesByDate -> viewModelScope.launch { onLoadQuotes(event.date) }
            is QuotesEvents.AddToFavorites -> viewModelScope.launch { onAddToFavorites(event.quote) }
            is QuotesEvents.RemoveFromFavorites -> viewModelScope.launch {
                onRemoveFromFavorites(
                    event.quote
                )
            }
            is QuotesEvents.SetSortedRules -> viewModelScope.launch {
                onSetSortedRules(
                    event.sortedField,
                    event.sortedOrder
                )
            }
        }
    }

    private suspend fun onRemoveFromFavorites(quote: Quote) {
        _state.update {
            it.copy(
                quotesHeader = it.quotesHeader?.copy(
                    quotes = it.quotesHeader.quotes.map { oldQuote ->
                        if (oldQuote.id == quote.id)
                            quote.copy(
                                isFavourite = false
                            )
                        else
                            oldQuote
                    }
                )
            )
        }
        repo.removeFavourite(quote.id)
    }

    private suspend fun onAddToFavorites(quote: Quote) {
        _state.update {
            it.copy(
                quotesHeader = it.quotesHeader?.copy(
                    quotes = it.quotesHeader.quotes.map { oldQuote ->
                        if (oldQuote.id == quote.id)
                            quote.copy(isFavourite = true)
                        else
                            oldQuote
                    }
                )
            )
        }
        repo.addFavourite(quote.id)
    }

    private fun onSetSortedRules(sortedField: SortedField, sortedOrder: SortedOrder) {
        _state.update {
            it.copy(
                sortedField = sortedField,
                sortedOrder = sortedOrder,
                quotesHeader = it.quotesHeader
            ).apply {
                quotesHeader?.sortQuotes(sortedField, sortedOrder)
            }
        }
    }

    private suspend fun onLoadQuotes(date: LocalDate? = null) {
        repo.getQuotes(date).collectLatest { response ->
            _state.update {
                when (response) {
                    is FlowResponse.Error -> it.copy(quotesLoadingError = response.error)
                    is FlowResponse.Loading -> it.copy(isQuotesLoading = response.isLoading)
                    is FlowResponse.Success -> it.copy(
                        quotesHeader = response.data.apply {
                            sortQuotes(
                                it.sortedField,
                                it.sortedOrder
                            )
                        }
                    )
                }
            }
        }
    }
}