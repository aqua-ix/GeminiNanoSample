package com.aqua_ix.gemini_nano_sample.data

import android.content.Context
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.generationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerativeModelFactory @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val settingsRepository: SettingsRepository
) {
    private var currentModel: GenerativeModel? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        scope.launch {
            settingsRepository.settings.collect { settings ->
                createNewModel(settings)
            }
        }
    }

    private fun createNewModel(settings: AiModelSettings) {
        currentModel?.close()
        currentModel = GenerativeModel(
            generationConfig = generationConfig {
                context = appContext
                temperature = settings.temperature
                topK = settings.topK
                maxOutputTokens = settings.maxOutputTokens
            }
        )
    }

    fun getModel(): GenerativeModel {
        return currentModel ?: GenerativeModel(
            generationConfig = generationConfig {
                context = appContext
                settingsRepository.settings.value.let { settings ->
                    temperature = settings.temperature
                    topK = settings.topK
                    maxOutputTokens = settings.maxOutputTokens
                }
            }
        ).also { currentModel = it }
    }
}
