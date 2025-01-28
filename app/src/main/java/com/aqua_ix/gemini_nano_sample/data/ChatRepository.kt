package com.aqua_ix.gemini_nano_sample.data

import com.aqua_ix.gemini_nano_sample.domain.model.ChatMessage
import com.google.ai.edge.aicore.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val generativeModel: GenerativeModel
) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())

    fun getMessages(): Flow<List<ChatMessage>> = _messages.asStateFlow()

    suspend fun sendMessage(message: String): Result<ChatMessage> = runCatching {
        val userMessage = ChatMessage(content = message, isFromUser = true)
        _messages.value += userMessage

        val response = generativeModel.generateContent(message)
        response.text?.let { responseText ->
            val aiMessage = ChatMessage(content = responseText, isFromUser = false)
            _messages.value += aiMessage
            return@runCatching aiMessage
        } ?: throw IllegalStateException("AI response was empty")
    }
}