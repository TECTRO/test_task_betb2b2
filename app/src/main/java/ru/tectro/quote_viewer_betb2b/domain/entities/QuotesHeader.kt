package ru.tectro.quote_viewer_betb2b.domain.entities

import org.joda.time.LocalDate

data class QuotesHeader(
    val date: LocalDate,
    val owner: String,
    val quotes: List<Quote>
)