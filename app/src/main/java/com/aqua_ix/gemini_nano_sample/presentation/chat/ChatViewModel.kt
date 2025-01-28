package com.aqua_ix.gemini_nano_sample.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aqua_ix.gemini_nano_sample.domain.usecase.ChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase
) : ViewModel() {
    val messages = chatUseCase.getMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun sendMessage(message: String) {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            chatUseCase.sendMessage(message)
                .onSuccess {
                    _uiState.value = ChatUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = ChatUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}

sealed class ChatUiState {
    data object Initial : ChatUiState()
    data object Loading : ChatUiState()
    data object Success : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}