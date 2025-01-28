package com.aqua_ix.gemini_nano_sample.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aqua_ix.gemini_nano_sample.data.AiModelSettings
import com.aqua_ix.gemini_nano_sample.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val settings = settingsRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AiModelSettings()
        )

    fun updateSettings(settings: AiModelSettings) {
        viewModelScope.launch {
            settingsRepository.updateSettings(settings)
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            settingsRepository.updateSettings(AiModelSettings())
        }
    }
}