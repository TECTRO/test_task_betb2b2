package ru.tectro.quote_viewer_betb2b.domain.datasources.datastore

import kotlinx.coroutines.flow.Flow
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedField
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedOrder

interface ISettingsManager {
    suspend fun setSortSettings(sortedField: SortedField, sortedOrder: SortedOrder)
    fun getSortSettings(): Flow<Pair<SortedField, SortedOrder>>
}