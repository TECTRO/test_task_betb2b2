package ru.tectro.quote_viewer_betb2b.domain.datasources.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.entities.FavouriteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.entities.QuoteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.entities.QuotesHeaderEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.FavouritesDao
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.QuotesDao

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