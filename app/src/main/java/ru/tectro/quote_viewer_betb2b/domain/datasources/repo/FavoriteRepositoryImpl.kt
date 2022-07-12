package ru.tectro.quote_viewer_betb2b.domain.datasources.repo

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.api.CbrApi
import ru.tectro.quote_viewer_betb2b.domain.datasources.api.util.toQuotesHeader
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.QuotesDatabase
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.entities.FavouriteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.util.toQuotesHeader
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.util.simplify
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.FlowResponse
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.Response
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.ResponseError
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.UpdateEvents
import ru.tectro.quote_viewer_betb2b.domain.entities.QuotesHeader
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val api: CbrApi,
    cache: QuotesDatabase
) : IFavoriteRepository {
    private val favouritesDao = cache.favouritesDao

    override suspend fun getFavouriteQuotes(date: LocalDate?) = flow {
        emit(FlowResponse.Loading())

        when (date) {
            null -> favouritesDao.getLatestFavoriteQuotes()
            else -> favouritesDao.getFavoriteQuotesByDate(date.toDate().time)
        }
            .toQuotesHeader()?.let {
                emit(FlowResponse.Success(it))
                emit(FlowResponse.Loading(false))
                return@flow
            }

        val favourites = favouritesDao.getFavourites().simplify()
        if (favourites.isEmpty()) {
            emit(FlowResponse.Error(ResponseError(404, null)))
            emit(FlowResponse.Loading(false))
            return@flow
        }

        val response = networkCall {
            when (date) {
                null -> api.getLatestQuotes()
                else -> api.getQuotesByDate(date.toString("dd/MM/yyyy"))
            }
        }

        when (response) {
            is Response.Error -> emit(FlowResponse.Error(response.error))
            is Response.Success -> {
                val favoriteQuotes = response.data.toQuotesHeader(favourites).run {
                    copy(
                        quotes = quotes.filter { it.isFavourite }
                    )
                }
                emit(FlowResponse.Success(favoriteQuotes))
            }
        }

        emit(FlowResponse.Loading(false))
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