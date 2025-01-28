package com.aqua_ix.gemini_nano_sample.data

import kotlinx.serialization.Serializable

@Serializable
data class AiModelSettings(
    val temperature: Float = 0.2f,
    val topK: Int = 16,
    val maxOutputTokens: Int = 256
)