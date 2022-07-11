package ru.tectro.quote_viewer_betb2b.domain.datasources.api.util

import org.joda.time.format.DateTimeFormat
import ru.tectro.quote_viewer_betb2b.domain.datasources.api.response.QuoteDTO
import ru.tectro.quote_viewer_betb2b.domain.datasources.api.response.QuotesHeaderDTO
import ru.tectro.quote_viewer_betb2b.domain.entities.Quote
import ru.tectro.quote_viewer_betb2b.domain.entities.QuotesHeader

private val formatter = DateTimeFormat.forPattern("dd.MM.yyyy")

fun QuoteDTO.toQuote(favorites: List<String>) = Quote(
    id, isoNumCode, isoCharCode, nominal, title, value.replace(',','.').toDouble(), favorites.contains(id)
)

fun QuotesHeaderDTO.toQuotesHeader(favorites: List<String>) = QuotesHeader(
    owner = owner,
    date = formatter.parseLocalDate(date),
    quotes = quotes.map { it.toQuote(favorites) }.sortedBy { it.id }
)