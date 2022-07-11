package ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.QuoteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.QuotesHeaderEntity

data class HeaderWithQuotes(
    @Embedded
    val header:QuotesHeaderEntity,
    @Relation(
        parentColumn = "date",
        entityColumn = "date"
    )
    val quotes:List<QuoteEntity>
)