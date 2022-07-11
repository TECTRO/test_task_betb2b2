package ru.tectro.quote_viewer_betb2b.domain.datasources.repo

import kotlinx.coroutines.flow.*
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.api.CbrApi
import ru.tectro.quote_viewer_betb2b.domain.datasources.api.util.toQuotesHeader
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.QuotesDatabase
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.util.simplify
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.util.toHeaderWithQuotes
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.util.toQuotesHeader
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.util.toQuotesHeader
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.FlowResponse
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuotesRepositoryImpl @Inject constructor(
    private val api: CbrApi,
    cache: QuotesDatabase
) : IQuotesRepository {

    private val favouritesDao = cache.favouritesDao
    private val quotesDao = cache.quotesDao

    override suspend fun getQuotes(date: LocalDate?) = flow {
        emit(FlowResponse.Loading())

        val favorites = favouritesDao.getFavourites().simplify()
        val cachedQuotes = when (date) {
            null -> quotesDao.getLatestQuotes()
            else -> quotesDao.getQuotesByDate(date.toDate().time)
        }
            ?.toQuotesHeader(favorites)

        cachedQuotes?.let {
            emit(FlowResponse.Success(it))
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
                val remoteQuotes = response.data.toQuotesHeader(favorites)
                if (cachedQuotes?.date != remoteQuotes.date) {
                    quotesDao.setQuotes(remoteQuotes.toHeaderWithQuotes())
                    emit(FlowResponse.Success(remoteQuotes))
                }
            }
        }

        emit(FlowResponse.Loading(false))
    }
}