@file:Suppress("DEPRECATION")

package ru.tectro.quote_viewer_betb2b.domain.di

import android.app.Application
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.create
import ru.tectro.quote_viewer_betb2b.domain.datasources.api.CbrApi
import ru.tectro.quote_viewer_betb2b.domain.datasources.cache.QuotesDatabase
import ru.tectro.quote_viewer_betb2b.domain.datasources.repo.QuotesRepositoryImpl
import ru.tectro.quote_viewer_betb2b.domain.datasources.repo.IQuotesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Suppress("DEPRECATION")
    @Singleton
    @Provides
    fun provideCbrApi(): CbrApi = Retrofit.Builder()
        .baseUrl(CbrApi.BASE_URL)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()
        .create()

    @Singleton
    @Provides
    fun provideQuotesDatabase(application: Application):QuotesDatabase = Room.databaseBuilder(
        application.applicationContext,
        QuotesDatabase::class.java,
        "${QuotesDatabase::class.simpleName}.db"
    ).build()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainRepoModule {

    @Singleton
    @Binds
    abstract fun bindCbrRepo(
        cbrRepositoryImpl: QuotesRepositoryImpl
    ): IQuotesRepository
}