package ru.tectro.quote_viewer_betb2b.main_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.repo.IFavoriteRepository
import ru.tectro.quote_viewer_betb2b.domain.datasources.repo.IQuotesRepository
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.FlowResponse
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.ResponseError
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.UpdateEvents
import ru.tectro.quote_viewer_betb2b.domain.entities.*
import ru.tectro.quote_viewer_betb2b.domain.datasources.datastore.ISettingsManager
import javax.inject.Inject

data class QuotesState(
    val date: LocalDate = LocalDate.now(),
    val owner: String? = null,
    val quotes: List<Quote> = emptyList(),

    val isQuotesLoading: Boolean = false,
    val quotesLoadingError: ResponseError? = null,

    val sortedField: SortedField = SortedField.Title,
    val sortedOrder: SortedOrder = SortedOrder.ASC
)

sealed class QuotesEvents {
    object LoadLatestQuotes : QuotesEvents()
    data class LoadQuotesByDate(val date: LocalDate) : QuotesEvents()
    object SuppressError : QuotesEvents()
    data class AddToFavorites(val quote: Quote) : QuotesEvents()
    data class RemoveFromFavorites(val quote: Quote) : QuotesEvents()

    object NextSortedOrder : QuotesEvents()
    data class SetSortedField(val sortedField: SortedField) : QuotesEvents()
}

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val quotesRepo: IQuotesRepository,
    private val favoritesRepo: IFavoriteRepository,
    private val settingsDataStore: ISettingsManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            launch {
                favoritesRepo.favouriteUpdateFlow.collectLatest { event ->
                    when (event) {
                        is UpdateEvents.Update,
                        is UpdateEvents.Add -> onAddToFavorites(event.data)
                        is UpdateEvents.Remove -> onRemoveFromFavorites(event.data)
                    }
                }
            }

            launch {
                settingsDataStore.getSortSettings().collectLatest {
                    _state.update { state ->
                        state.copy(
                            sortedOrder = it.second,
                            sortedField = it.first,
                            quotes = state.quotes.sortQuotes(it.first, it.second)
                        )
                    }
                }
            }
        }
    }

    private val _state = MutableStateFlow(QuotesState())
    val state = _state.asStateFlow()

    fun onEvent(event: QuotesEvents) {
        when (event) {
            QuotesEvents.LoadLatestQuotes -> viewModelScope.launch { onLoadQuotes() }
            is QuotesEvents.LoadQuotesByDate -> viewModelScope.launch { onLoadQuotes(event.date) }
            is QuotesEvents.AddToFavorites -> viewModelScope.launch {
                favoritesRepo.addFavourite(
                    event.quote.id
                )
            }
            is QuotesEvents.RemoveFromFavorites -> viewModelScope.launch {
                favoritesRepo.removeFavourite(
                    event.quote.id
                )
            }

            is QuotesEvents.SetSortedField -> viewModelScope.launch {
                settingsDataStore.setSortSettings(
                    event.sortedField,
                    state.value.sortedOrder
                )
            }

            QuotesEvents.NextSortedOrder -> viewModelScope.launch {
                var nextIndex = state.value.sortedOrder.ordinal + 1
                if (nextIndex > SortedOrder.values().lastIndex)
                    nextIndex = 0
                settingsDataStore.setSortSettings(
                    state.value.sortedField,
                    SortedOrder.values()[nextIndex]
                )
            }
            QuotesEvents.SuppressError -> _state.update {
                it.copy(
                    quotesLoadingError = null
                )
            }
        }
    }

    private fun onAddToFavorites(quoteId: String) {
        _state.update {
            it.copy(
                quotes = it.quotes.map { quote ->
                    if (quote.id == quoteId)
                        return@map quote.copy(isFavourite = true)
                    quote
                }
            )
        }
    }

    private fun onRemoveFromFavorites(quoteId: String) {
        _state.update {
            it.copy(
                quotes = it.quotes.map { quote ->
                    if (quote.id == quoteId)
                        return@map quote.copy(isFavourite = false)
                    quote
                }
            )
        }
    }

    private suspend fun onLoadQuotes(date: LocalDate? = null) {
        quotesRepo.getQuotes(date).collectLatest { response ->
            _state.update {
                when (response) {
                    is FlowResponse.Error -> it.copy(quotesLoadingError = response.error)
                    is FlowResponse.Loading -> it.copy(isQuotesLoading = response.isLoading)
                    is FlowResponse.Success -> it.copy(
                        date = response.data.date,
                        quotes = response.data.quotes.sortQuotes(it.sortedField, it.sortedOrder),
                        owner = response.data.owner,
                    )
                }
            }
        }
    }
}