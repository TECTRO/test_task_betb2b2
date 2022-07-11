package ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuotesHeaderEntity(
    @PrimaryKey
    val date:Long,
    val owner:String
)