package ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes

import androidx.room.*
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.entities.QuoteEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.entities.QuotesHeaderEntity
import ru.tectro.quote_viewer_betb2b.domain.datasources.db.quotes.entities.relations.HeaderWithQuotes

@Dao
interface QuotesDao {

    @Transaction
    @Query("SELECT * from QuotesHeaderEntity order by date desc limit 1")
    suspend fun getLatestQuotes(): HeaderWithQuotes?

    @Transaction
    @Query("select * from QuotesHeaderEntity where date == :date limit 1")
    suspend fun getQuotesByDate(date: Long): HeaderWithQuotes?

    @Transaction
    suspend fun setQuotes(quotes: HeaderWithQuotes){
        setQuotesHeader(quotes.header)
        setQuotes(quotes.quotes)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setQuotes(quotes: List<QuoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setQuotesHeader(header: QuotesHeaderEntity)


}