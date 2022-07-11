package ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.util

import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.entities.FavouriteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.entities.FavouriteQuotesEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.util.toQuote
import ru.tectro.quote_viewer_betb2b.domain.entities.QuotesHeader
import java.util.*

fun String.toFavoriteEntity() = FavouriteEntity(this)

fun List<FavouriteQuotesEntity>.toQuotesHeader() =
    groupBy { it.date to it.owner }.entries.map { entry ->
        QuotesHeader(
            LocalDate.fromDateFields(Date(entry.key.first)),
            entry.key.second,
            entry.value.map { it.toQuote() }
        )
    }.firstOrNull()