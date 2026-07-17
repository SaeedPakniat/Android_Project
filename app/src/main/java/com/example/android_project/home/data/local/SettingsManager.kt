package com.example.android_project.home.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val SORT_TYPE = stringPreferencesKey("sort_type")
        val SORT_ENABLED = booleanPreferencesKey("sort_enabled")
    }

    suspend fun setSortType(type: String) {
        context.dataStore.edit { preferences ->
            preferences[SORT_TYPE] = type
        }
    }

    suspend fun getSortType(): String {
        return context.dataStore.data.map { preferences ->
            preferences[SORT_TYPE] ?: "IMDb"
        }.first()
    }

    suspend fun setSortEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ENABLED] = enabled
        }
    }

    suspend fun isSortEnabled(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[SORT_ENABLED] ?: false
        }.first()
    }
}
