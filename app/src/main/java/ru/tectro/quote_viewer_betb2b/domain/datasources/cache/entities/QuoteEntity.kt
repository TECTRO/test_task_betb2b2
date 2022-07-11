package ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities

import androidx.room.Entity

@Entity(
    primaryKeys = [
        "date",
        "id"
    ]
)
data class QuoteEntity(
    val date:Long,
    val id:String,

    val isoNumCode: Int = 0,

    val isoCharCode: String = "",

    val nominal: Int = 0,

    val title: String = "",

    val value: Double = 0.0
)
