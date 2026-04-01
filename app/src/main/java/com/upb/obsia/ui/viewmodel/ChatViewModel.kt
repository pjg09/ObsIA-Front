package com.upb.obsia.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upb.obsia.data.AuthPreferences
import com.upb.obsia.data.ChatMessage
import com.upb.obsia.domain.model.EngineResponse
import com.upb.obsia.domain.repository.ChatRepository
import com.upb.obsia.domain.repository.EngineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatInitState {
    object CopyingAssets : ChatInitState()
    object Initializing : ChatInitState()
    object Ready : ChatInitState()
    data class Error(val message: String) : ChatInitState()
}

sealed class ChatQueryState {
    object Idle : ChatQueryState()
    object Loading : ChatQueryState()
    data class Error(val message: String) : ChatQueryState()
}

class ChatViewModel(
        private val context: Context,
        private val engineRepository: EngineRepository,
        private val chatRepository: ChatRepository
) : ViewModel() {

    private val _initState = MutableStateFlow<ChatInitState>(ChatInitState.CopyingAssets)
    val initState: StateFlow<ChatInitState> = _initState.asStateFlow()

    private val _queryState = MutableStateFlow<ChatQueryState>(ChatQueryState.Idle)
    val queryState: StateFlow<ChatQueryState> = _queryState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _sessionName = MutableStateFlow("Chat")
    val sessionName: StateFlow<String> = _sessionName.asStateFlow()

    val welcomeMessage = "¡Arro está aquí para ayudarte!"

    private var sessionId: Int = -1

    fun initialize(sessionId: Int) {
        if (_initState.value is ChatInitState.Ready) return

        this.sessionId = sessionId

        val userId = AuthPreferences.getUserId(context)
        if (userId == -1) {
            _initState.value = ChatInitState.Error("No hay sesión de usuario activa.")
            return
        }

        viewModelScope.launch {
            _sessionName.value = chatRepository.getSessionName(sessionId) ?: "Chat"

            _initState.value = ChatInitState.Initializing
            val engineReady = engineRepository.initialize()

            if (!engineReady) {
                _initState.value = ChatInitState.Error("El motor no pudo inicializarse.")
                return@launch
            }

            _messages.value = chatRepository.getMessages(sessionId)
            _initState.value = ChatInitState.Ready
        }
    }

    fun sendMessage(text: String) {
        if (_initState.value !is ChatInitState.Ready) return
        if (_queryState.value is ChatQueryState.Loading) return
        if (text.isBlank()) return

        viewModelScope.launch {
            val userMessage =
                    ChatMessage(sessionId = sessionId, role = "user", content = text.trim())
            val userMsgId = chatRepository.saveMessage(userMessage)
            _messages.value = _messages.value + userMessage.copy(id = userMsgId.toInt())

            chatRepository.touchSession(sessionId)
            _queryState.value = ChatQueryState.Loading

            when (val response = engineRepository.query(text.trim())) {
                is EngineResponse.Success -> {
                    val assistantMessage =
                            ChatMessage(
                                    sessionId = sessionId,
                                    role = "assistant",
                                    content = response.responseText,
                                    processingMs = response.processingMs
                            )
                    val assistantMsgId = chatRepository.saveMessage(assistantMessage)
                    _messages.value =
                            _messages.value + assistantMessage.copy(id = assistantMsgId.toInt())
                    _queryState.value = ChatQueryState.Idle
                }
                is EngineResponse.Failure -> {
                    _queryState.value = ChatQueryState.Error(response.errorMessage)
                }
            }
        }
    }
}
