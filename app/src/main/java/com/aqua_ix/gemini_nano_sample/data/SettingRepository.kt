package com.aqua_ix.gemini_nano_sample.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "settings"
        )
    }

    private val settingsKey = stringPreferencesKey("ai_model_settings")

    private val _settings = MutableStateFlow(AiModelSettings())
    val settings = _settings.asStateFlow()

    init {
        context.dataStore.data
            .map { preferences ->
                preferences[settingsKey]?.let { jsonString ->
                    Json.decodeFromString<AiModelSettings>(jsonString)
                } ?: AiModelSettings()
            }
            .onEach { settings ->
                _settings.value = settings
            }
    }

    suspend fun updateSettings(newSettings: AiModelSettings) {
        context.dataStore.edit { preferences ->
            preferences[settingsKey] = Json.encodeToString(newSettings)
        }
        _settings.value = newSettings
    }
}