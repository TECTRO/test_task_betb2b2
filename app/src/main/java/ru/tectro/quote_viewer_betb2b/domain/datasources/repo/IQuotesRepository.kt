package ru.tectro.quote_viewer_betb2b.domain.datasources.repo

import kotlinx.coroutines.flow.Flow
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.FlowResponse
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.UpdateEvents
import ru.tectro.quote_viewer_betb2b.domain.entities.QuotesHeader

interface IQuotesRepository {
    suspend fun getQuotes(date: LocalDate? = null): Flow<FlowResponse<QuotesHeader>>
}