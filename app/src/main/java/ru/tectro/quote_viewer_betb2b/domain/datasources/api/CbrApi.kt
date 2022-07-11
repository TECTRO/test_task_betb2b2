package ru.tectro.quote_viewer_betb2b.domain.datasources.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.tectro.quote_viewer_betb2b.domain.datasources.api.response.QuotesHeaderDTO

interface CbrApi {
    companion object {
        const val BASE_URL = "http://www.cbr.ru/scripts/"
    }

    @GET("XML_daily.asp")
    suspend fun getQuotesByDate(
        @Query("date_req") date:String
    ):QuotesHeaderDTO

    @GET("XML_daily.asp")
    suspend fun getLatestQuotes(
    ):QuotesHeaderDTO

}