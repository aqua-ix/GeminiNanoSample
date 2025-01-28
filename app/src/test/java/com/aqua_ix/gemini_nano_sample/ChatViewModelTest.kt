package com.aqua_ix.gemini_nano_sample

import app.cash.turbine.test
import com.aqua_ix.gemini_nano_sample.domain.model.ChatMessage
import com.aqua_ix.gemini_nano_sample.domain.usecase.ChatUseCase
import com.aqua_ix.gemini_nano_sample.presentation.chat.ChatUiState
import com.aqua_ix.gemini_nano_sample.presentation.chat.ChatViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var chatUseCase: ChatUseCase
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        chatUseCase = mockk {
            every { getMessages() } returns flowOf(emptyList())
        }
        viewModel = ChatViewModel(chatUseCase)
    }

    @Test
    fun `initial state should be Initial`() = runTest {
        viewModel.uiState.test {
            assertEquals(ChatUiState.Initial, awaitItem())
        }
    }

    @Test
    fun `sendMessage success should update state`() = runTest {
        // given
        val message = "Hello"
        coEvery {
            chatUseCase.sendMessage(message)
        } returns Result.success(ChatMessage(content = "Hi", isFromUser = false))

        // Collect states
        viewModel.uiState.test {
            assertEquals(ChatUiState.Initial, awaitItem())

            // when
            viewModel.sendMessage(message)

            // then
            assertEquals(ChatUiState.Loading, awaitItem())
            assertEquals(ChatUiState.Success, awaitItem())
        }
    }

    @Test
    fun `sendMessage failure should update error state`() = runTest {
        // given
        val message = "Hello"
        val error = RuntimeException("Error")
        coEvery {
            chatUseCase.sendMessage(message)
        } returns Result.failure(error)

        // Collect states
        viewModel.uiState.test {
            assertEquals(ChatUiState.Initial, awaitItem())

            // when
            viewModel.sendMessage(message)

            // then
            assertEquals(ChatUiState.Loading, awaitItem())
            assertEquals(
                ChatUiState.Error(error.message ?: "Unknown error"),
                awaitItem()
            )
        }
    }
}