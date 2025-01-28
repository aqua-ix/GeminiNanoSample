package com.aqua_ix.gemini_nano_sample.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aqua_ix.gemini_nano_sample.presentation.chat.components.ChatMessageList
import com.aqua_ix.gemini_nano_sample.presentation.chat.components.MessageInput

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        if (uiState is ChatUiState.Error) {
            ErrorBanner((uiState as ChatUiState.Error).message)
        }

        ChatMessageList(
            messages = messages,
            modifier = Modifier.weight(1f)
        )

        MessageInput(
            onSendMessage = viewModel::sendMessage,
            isLoading = uiState is ChatUiState.Loading
        )
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onError
        )
    }
}