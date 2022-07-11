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
                        is UpdateEvents.Remove -> _state.update {
                            val favorites = favoritesRepo.getFavouriteQuotes(null)

                            val quotes = favorites?.quotes.orEmpty()
                            val date = favorites?.date
                            val owner = favorites?.owner
                            it.copy(
                                date = date,
                                quotes = quotes,
                                owner = owner
                            )
                        }
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
            is FavouritesEvents.LoadFavouritesByDate -> viewModelScope.launch {
                _state.update {
                    val favorites = favoritesRepo.getFavouriteQuotes(event.date)

                    val quotes = favorites?.quotes.orEmpty()
                    val date = favorites?.date
                    val owner = favorites?.owner
                    it.copy(
                        date = date,
                        quotes = quotes,
                        owner = owner
                    )
                }
            }

            FavouritesEvents.LoadLatestFavourites -> viewModelScope.launch {
                _state.update {
                    val favorites = favoritesRepo.getFavouriteQuotes(null)

                    val quotes = favorites?.quotes.orEmpty()
                    val date = favorites?.date
                    val owner = favorites?.owner
                    it.copy(
                        date = date,
                        quotes = quotes,
                        owner = owner
                    )
                }
            }

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
}