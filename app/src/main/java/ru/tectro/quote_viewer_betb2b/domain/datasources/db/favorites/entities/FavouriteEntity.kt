package ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavouriteEntity(@PrimaryKey val QuoteId:String)