package ru.tectro.quote_viewer_betb2b.domain.datasources.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.FavouriteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.QuoteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.entities.QuotesHeaderEntity

@Database(
    entities = [
        FavouriteEntity::class,
        QuoteEntity::class,
        QuotesHeaderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class QuotesDatabase : RoomDatabase() {
    abstract val favouritesDao: FavouritesDao
    abstract val quotesDao: QuotesDao
}