package ru.tectro.quote_viewer_betb2b.domain.datasources.cache.util

import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.FavouriteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.FavouriteQuotesEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.QuoteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.QuotesHeaderEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.relations.HeaderWithQuotes
import ru.tectro.quote_viewer_betb2b.domain.entities.Quote
import ru.tectro.quote_viewer_betb2b.domain.entities.QuotesHeader
import java.util.*

fun List<FavouriteEntity>.simplify() = map { it.QuoteId }

fun QuotesHeader.toHeader() = QuotesHeaderEntity(
    owner = owner,
    date = date.toDate().time
)

fun Quote.toQuoteEntity(date: LocalDate) = QuoteEntity(
    date = date.toDate().time, id, isoNumCode, isoCharCode, nominal, title, value
)

fun QuotesHeader.toHeaderWithQuotes() = HeaderWithQuotes(
    header = toHeader(),
    quotes = quotes.map { it.toQuoteEntity(date) }
)

fun QuoteEntity.toQuote(favorites: List<String>) = Quote(
    id, isoNumCode, isoCharCode, nominal, title, value, isFavourite = favorites.contains(id)
)

fun HeaderWithQuotes.toQuotesHeader(favorites: List<String>) = QuotesHeader(
    quotes = quotes.map { it.toQuote(favorites) }.sortedBy { it.id },
    owner = header.owner,
    date = LocalDate.fromDateFields(Date(header.date))
)

fun FavouriteQuotesEntity.toQuote() = Quote(
    id = id,
    isoNumCode = isoNumCode,
    isFavourite = true,
    isoCharCode = isoCharCode,
    nominal = nominal,
    title = title,
    value = value
)

fun List<FavouriteQuotesEntity>.toQuotesHeader() =
    groupBy { it.date to it.owner }.entries.map { entry ->
        QuotesHeader(
            LocalDate.fromDateFields(Date(entry.key.first)),
            entry.key.second,
            entry.value.map { it.toQuote() }
        )
    }