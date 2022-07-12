package ru.tectro.quote_viewer_betb2b.favourites_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.datastore.ISettingsManager
import ru.tectro.quote_viewer_betb2b.domain.datasources.repo.IFavoriteRepository
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.FlowResponse
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.ResponseError
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.UpdateEvents
import ru.tectro.quote_viewer_betb2b.domain.entities.Quote
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedField
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedOrder
import ru.tectro.quote_viewer_betb2b.domain.entities.sortQuotes
import ru.tectro.quote_viewer_betb2b.main_screen.viewmodel.QuotesEvents
import javax.inject.Inject

data class FavouritesState(
    val date: LocalDate? = null,
    val owner: String? = null,
    val quotes: List<Quote> = emptyList(),
    val loadingError: ResponseError? = null,
    val isQuotesLoading: Boolean = false,

    val sortedField: SortedField = SortedField.IsFavourite,
    val sortedOrder: SortedOrder = SortedOrder.DESC
)

sealed class FavouritesEvents {
    object LoadLatestFavourites : FavouritesEvents()
    data class LoadFavouritesByDate(val date: LocalDate) : FavouritesEvents()
    data class RemoveFromFavorites(val quote: Quote) : FavouritesEvents()

    object NextSortedOrder : FavouritesEvents()
    data class SetSortedField(val sortedField: SortedField) : FavouritesEvents()
}

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val favoritesRepo: IFavoriteRepository,
    private val settingsManager: ISettingsManager
) : ViewModel() {
    private val _state = MutableStateFlow(FavouritesState())
    val state = _state.asStateFlow()

    init {
        onEvent(FavouritesEvents.LoadLatestFavourites)

        viewModelScope.launch {
            launch {
                favoritesRepo.favouriteUpdateFlow.collectLatest { update ->
                    when (update) {
                        is UpdateEvents.Update,
                        is UpdateEvents.Add,
                        is UpdateEvents.Remove ->
                            onLoadFavoriteQuotes(state.value.date)
                    }
                }
            }
            launch {
                settingsManager.getSortSettings().collectLatest {
                    _state.update { state ->
                        state.copy(
                            sortedField = it.first,
                            sortedOrder = it.second,
                            quotes = state.quotes.sortQuotes(it.first, it.second)
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: FavouritesEvents) {
        when (event) {
            is FavouritesEvents.LoadFavouritesByDate -> onLoadFavoriteQuotes(event.date)

            FavouritesEvents.LoadLatestFavourites -> onLoadFavoriteQuotes()

            is FavouritesEvents.RemoveFromFavorites -> viewModelScope.launch {
                favoritesRepo.removeFavourite(event.quote.id)
            }

            is FavouritesEvents.SetSortedField -> viewModelScope.launch {
                settingsManager.setSortSettings(
                    event.sortedField,
                    state.value.sortedOrder
                )
            }

            FavouritesEvents.NextSortedOrder -> viewModelScope.launch {
                var nextIndex = state.value.sortedOrder.ordinal + 1
                if (nextIndex > SortedOrder.values().lastIndex)
                    nextIndex = 0
                settingsManager.setSortSettings(
                    state.value.sortedField,
                    SortedOrder.values()[nextIndex]
                )
            }
        }
    }

    private fun onLoadFavoriteQuotes(date: LocalDate? = null) = viewModelScope.launch {
        favoritesRepo.getFavouriteQuotes(date).collectLatest { response ->
            when (response) {
                is FlowResponse.Error -> _state.update {
                    it.copy(
                        loadingError = response.error
                    )
                }
                is FlowResponse.Loading -> _state.update {
                    it.copy(
                        isQuotesLoading = response.isLoading
                    )
                }
                is FlowResponse.Success -> {
                    _state.update {
                        val favorites = response.data

                        it.copy(
                            date = favorites.date,
                            quotes = favorites.quotes,
                            owner = favorites.owner
                        )
                    }
                }
            }
        }
    }
}