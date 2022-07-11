package ru.tectro.quote_viewer_betb2b.domain.entities

data class Quote(
    val id:String,
    val isoNumCode: Int,
    val isoCharCode: String,
    val nominal: Int,
    val title: String,
    val value: Double,
    val isFavourite:Boolean
)
