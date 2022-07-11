package ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites

import androidx.room.*
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.entities.FavouriteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.favorites.entities.FavouriteQuotesEntity

@Dao
interface FavouritesDao {
    @Query("Select * from FavouriteEntity")
    suspend fun getFavourites(): List<FavouriteEntity>

    @Query(
        """
            select headerDate as date,headerOwner as owner,id,isoNumCode,isoCharCode,nominal,title,value from (
                select * from (select QuotesHeaderEntity.date as headerDate, QuotesHeaderEntity.owner as headerOwner from QuotesHeaderEntity order by date desc limit 1)
                    left join QuoteEntity 
                        on headerDate == QuoteEntity.date 
                    inner join FavouriteEntity 
                        on QuoteEntity.id == FavouriteEntity.QuoteId
            )
        """
    )
    suspend fun getLatestFavoriteQuotes(): List<FavouriteQuotesEntity>

    @Query(
        """
            select  QuotesHeaderEntity.date as date, owner, id, isoNumCode,isoCharCode,nominal,title,value from QuoteEntity 
                inner join FavouriteEntity 
                    on QuoteEntity.id == FavouriteEntity.QuoteId
                left join QuotesHeaderEntity
                    on QuotesHeaderEntity.date == QuoteEntity.date
                where QuoteEntity.date == :date
        """
    )
    suspend fun getFavoriteQuotesByDate(date: Long): List<FavouriteQuotesEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavourite(favouriteEntity: FavouriteEntity)

    @Delete
    suspend fun removeFavourite(favouriteEntity: FavouriteEntity)
}