package ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.entities.QuoteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.entities.QuotesHeaderEntity

data class HeaderWithQuotes(
    @Embedded
    val header: QuotesHeaderEntity,
    @Relation(
        parentColumn = "date",
        entityColumn = "date"
    )
    val quotes:List<QuoteEntity>
)