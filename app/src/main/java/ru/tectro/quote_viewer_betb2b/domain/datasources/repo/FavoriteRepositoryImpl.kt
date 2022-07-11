package ru.tectro.quote_viewer_betb2b.domain.datasources.repo

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.QuotesDatabase
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.entities.FavouriteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.util.toQuotesHeader
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.UpdateEvents
import ru.tectro.quote_viewer_betb2b.domain.entities.QuotesHeader
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(cache: QuotesDatabase) : IFavoriteRepository {
    private val favouritesDao = cache.favouritesDao

    override suspend fun getFavouriteQuotes(date: LocalDate?): QuotesHeader? {
        return when (date) {
            null -> favouritesDao.getLatestFavoriteQuotes()
            else -> favouritesDao.getFavoriteQuotesByDate(date.toDate().time)
        }
            .toQuotesHeader()
    }

    private val mutableFavouriteUpdateFlow = MutableSharedFlow<UpdateEvents<String>>()
    override val favouriteUpdateFlow = mutableFavouriteUpdateFlow.asSharedFlow()

    override suspend fun addFavourite(quoteId: String) {
        favouritesDao.addFavourite(
            FavouriteEntity(quoteId)
        )
        mutableFavouriteUpdateFlow.emit(UpdateEvents.Add(quoteId))
    }

    override suspend fun removeFavourite(quoteId: String) {
        favouritesDao.removeFavourite(
            FavouriteEntity(quoteId)
        )
        mutableFavouriteUpdateFlow.emit(UpdateEvents.Remove(quoteId))
    }
}