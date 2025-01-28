package com.aqua_ix.gemini_nano_sample.domain.usecase

import com.aqua_ix.gemini_nano_sample.data.ChatRepository
import com.aqua_ix.gemini_nano_sample.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend fun sendMessage(message: String): Result<ChatMessage> {
        return repository.sendMessage(message)
    }

    fun getMessages(): Flow<List<ChatMessage>> = repository.getMessages()
}