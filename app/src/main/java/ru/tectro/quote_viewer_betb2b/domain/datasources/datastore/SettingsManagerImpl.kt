package ru.tectro.quote_viewer_betb2b.domain.datasources.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedField
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedOrder
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("Main_screen_sort_store")

class SettingsManagerImpl @Inject constructor(@ApplicationContext context: Context) :
    ISettingsManager {

    private object PreferenceKeys {
        val SORTED_FIELD = stringPreferencesKey("SORTED_FIELD")
        val SORTED_ORDER = stringPreferencesKey("SORTED_ORDER")
    }

    private val sortDataStore = context.dataStore

    override suspend fun setSortSettings(sortedField: SortedField, sortedOrder: SortedOrder) {
        sortDataStore.edit {
            it[PreferenceKeys.SORTED_FIELD] = sortedField.name
            it[PreferenceKeys.SORTED_ORDER] = sortedOrder.name
        }
    }

    override fun getSortSettings() = sortDataStore.data.map {
        val field =
            it[PreferenceKeys.SORTED_FIELD]?.let { field -> enumValueOf(field) }
                ?: SortedField.Title
        val order =
            it[PreferenceKeys.SORTED_ORDER]?.let { order -> enumValueOf(order) }
                ?: SortedOrder.ASC

        field to order
    }
}