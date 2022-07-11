package ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuotesHeaderEntity(
    @PrimaryKey
    val date:Long,
    val owner:String
)