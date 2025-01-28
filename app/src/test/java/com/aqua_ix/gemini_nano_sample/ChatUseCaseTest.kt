package com.aqua_ix.gemini_nano_sample

import app.cash.turbine.test
import com.aqua_ix.gemini_nano_sample.data.ChatRepository
import com.aqua_ix.gemini_nano_sample.domain.model.ChatMessage
import com.aqua_ix.gemini_nano_sample.domain.usecase.ChatUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ChatUseCaseTest {
    private lateinit var repository: ChatRepository
    private lateinit var useCase: ChatUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ChatUseCase(repository)
    }

    @Test
    fun `generateResponse should return repository result`() = runTest {
        // given
        val message = "Hello"
        val expectedResponse = Result.success(
            ChatMessage(content = "Hi", isFromUser = false)
        )
        coEvery {
            repository.sendMessage(message)
        } returns expectedResponse

        // when
        val result = useCase.sendMessage(message)

        // then
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `getMessages should return repository flow`() = runTest {
        // given
        val messages = listOf(
            ChatMessage(content = "Hello", isFromUser = true),
            ChatMessage(content = "Hi", isFromUser = false)
        )
        every {
            repository.getMessages()
        } returns flowOf(messages)

        // when
        val result = useCase.getMessages()

        // then
        result.test {
            assertEquals(messages, awaitItem())
            awaitComplete()
        }
    }
}