// Ruta: app/src/main/java/com/upb/obsia/ui/viewmodel/ChatViewModel.kt

package com.upb.obsia.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obsIA.engine.NativeEngine
import com.upb.obsia.data.AppDatabase
import com.upb.obsia.data.ChatMessage
import com.upb.obsia.data.SessionManager
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

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

class ChatViewModel : ViewModel() {

    private val engine = NativeEngine()

    private val _initState = MutableStateFlow<ChatInitState>(ChatInitState.CopyingAssets)
    val initState: StateFlow<ChatInitState> = _initState.asStateFlow()

    private val _queryState = MutableStateFlow<ChatQueryState>(ChatQueryState.Idle)
    val queryState: StateFlow<ChatQueryState> = _queryState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Nombre de la sesión resuelto desde la DB — la UI lo observa
    private val _sessionName = MutableStateFlow("Chat")
    val sessionName: StateFlow<String> = _sessionName.asStateFlow()

    val welcomeMessage = "¡Arro está aquí para ayudarte!"

    private var sessionId: Int = -1
    private var userId: Int = -1

    /**
     * Punto de entrada. Resuelve userId desde SessionManager y sessionName desde la DB. Solo
     * requiere sessionId desde la navegación.
     */
    fun initialize(context: Context, sessionId: Int) {
        this.sessionId = sessionId
        this.userId = SessionManager.getUserId(context)

        if (userId == -1) {
            _initState.value = ChatInitState.Error("No hay sesión de usuario activa.")
            return
        }

        viewModelScope.launch {
            try {
                // Resolver nombre de la sesión desde la DB
                val db = AppDatabase.getInstance(context)
                withContext(Dispatchers.IO) { db.chatSessionDao().getById(sessionId) }?.let {
                        session ->
                    _sessionName.value = session.title
                }

                // Paso 1: Copiar assets si no existen
                _initState.value = ChatInitState.CopyingAssets
                val modelFile = copyAssetIfNeeded(context, "qwen-medicina-q2k.gguf")
                val chunksFile = copyAssetIfNeeded(context, "chunks.json")

                // Paso 2: Inicializar motor JNI
                _initState.value = ChatInitState.Initializing
                val result =
                        withContext(Dispatchers.IO) {
                            engine.init(
                                    modelPath = modelFile.absolutePath,
                                    ragPath = chunksFile.absolutePath,
                                    nThreads = 4
                            )
                        }

                if (result != 0) {
                    _initState.value =
                            ChatInitState.Error("El motor no pudo inicializarse (código $result)")
                    return@launch
                }

                // Paso 3: Cargar historial
                loadMessages(context)

                _initState.value = ChatInitState.Ready
            } catch (e: Exception) {
                _initState.value =
                        ChatInitState.Error(e.message ?: "Error desconocido al inicializar")
            }
        }
    }

    fun sendMessage(context: Context, text: String) {
        if (_initState.value !is ChatInitState.Ready) return
        if (_queryState.value is ChatQueryState.Loading) return
        if (text.isBlank()) return

        viewModelScope.launch {
            val db = AppDatabase.getInstance(context)

            val userMessage =
                    ChatMessage(sessionId = sessionId, role = "user", content = text.trim())
            val userMsgId = withContext(Dispatchers.IO) { db.chatMessageDao().insert(userMessage) }
            _messages.value = _messages.value + userMessage.copy(id = userMsgId.toInt())

            withContext(Dispatchers.IO) {
                val session = db.chatSessionDao().getById(sessionId)
                session?.let {
                    db.chatSessionDao().update(it.copy(updatedAt = System.currentTimeMillis()))
                }
            }

            _queryState.value = ChatQueryState.Loading
            try {
                val rawJson = withContext(Dispatchers.IO) { engine.processQuery(text.trim()) }
                val json = JSONObject(rawJson)
                val status = json.optString("status")

                if (status == "ok") {
                    val responseText = json.optString("response_text")
                    val processingMs = json.optLong("processing_ms")
                    val assistantMessage =
                            ChatMessage(
                                    sessionId = sessionId,
                                    role = "assistant",
                                    content = responseText,
                                    processingMs = processingMs
                            )
                    val assistantMsgId =
                            withContext(Dispatchers.IO) {
                                db.chatMessageDao().insert(assistantMessage)
                            }
                    _messages.value =
                            _messages.value + assistantMessage.copy(id = assistantMsgId.toInt())
                    _queryState.value = ChatQueryState.Idle
                } else {
                    val errorMsg = json.optString("error_message", "Error al procesar la consulta")
                    _queryState.value = ChatQueryState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _queryState.value =
                        ChatQueryState.Error(e.message ?: "Error al comunicarse con el motor")
            }
        }
    }

    private suspend fun loadMessages(context: Context) {
        val db = AppDatabase.getInstance(context)
        val history =
                withContext(Dispatchers.IO) {
                    db.chatMessageDao().getMessagesBySessionOnce(sessionId)
                }
        _messages.value = history
    }

    private suspend fun copyAssetIfNeeded(context: Context, assetName: String): File {
        return withContext(Dispatchers.IO) {
            val destFile = File(context.filesDir, assetName)
            if (!destFile.exists()) {
                context.assets.open(assetName).use { input ->
                    FileOutputStream(destFile).use { output -> input.copyTo(output) }
                }
            }
            destFile
        }
    }

    override fun onCleared() {
        super.onCleared()
        engine.release()
    }
}
