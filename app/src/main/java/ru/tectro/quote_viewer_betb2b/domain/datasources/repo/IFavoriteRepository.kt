package ru.tectro.quote_viewer_betb2b.domain.datasources.repo

import kotlinx.coroutines.flow.SharedFlow
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.UpdateEvents
import ru.tectro.quote_viewer_betb2b.domain.entities.QuotesHeader

interface IFavoriteRepository {
    val favouriteUpdateFlow: SharedFlow<UpdateEvents<String>>
    suspend fun getFavouriteQuotes(date: LocalDate?): QuotesHeader?

    suspend fun addFavourite(quoteId: String)

    suspend fun removeFavourite(quoteId: String)
}