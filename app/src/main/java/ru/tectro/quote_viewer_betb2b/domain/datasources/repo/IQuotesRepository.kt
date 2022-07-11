package ru.tectro.quote_viewer_betb2b.domain.datasources.repo

import kotlinx.coroutines.flow.Flow
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.FlowResponse
import ru.tectro.quote_viewer_betb2b.domain.entities.QuotesHeader

interface IQuotesRepository {
    suspend fun getQuotes(date: LocalDate?): Flow<FlowResponse<QuotesHeader>>
    suspend fun addFavourite(quoteId: String)
    suspend fun removeFavourite(quoteId: String)
    suspend fun getFavouriteQuotes(date: LocalDate?): QuotesHeader?
}