package ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities

data class FavouriteQuotesEntity(
    val date: Long,
    val owner: String,
    val id: String,
    val isoNumCode: Int,
    val isoCharCode: String,
    val nominal: Int,
    val title: String,
    val value: Double
)