// Ruta: app/src/main/java/com/upb/obsia/ui/viewmodel/ChatListViewModel.kt

package com.upb.obsia.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upb.obsia.data.AppDatabase
import com.upb.obsia.data.ChatSession
import com.upb.obsia.data.AuthPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatListViewModel : ViewModel() {

    // Búsqueda en tiempo real
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Lista de sesiones filtradas que observa la UI
    private val _sessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val sessions: StateFlow<List<ChatSession>> = _sessions.asStateFlow()

    // Id de la sesión recién creada para navegar a ChatScreen
    private val _newSessionId = MutableStateFlow<Int?>(null)
    val newSessionId: StateFlow<Int?> = _newSessionId.asStateFlow()

    // Sesión seleccionada para el diálogo de renombrar
    private val _selectedSession = MutableStateFlow<ChatSession?>(null)
    val selectedSession: StateFlow<ChatSession?> = _selectedSession.asStateFlow()

    // Errores no fatales para mostrar en Snackbar
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Mapa sessionId -> preview del último mensaje (máx 40 chars)
    private val _lastMessagePreviews = MutableStateFlow<Map<Int, String>>(emptyMap())
    val lastMessagePreviews: StateFlow<Map<Int, String>> = _lastMessagePreviews.asStateFlow()

    private var rawSessions: List<ChatSession> = emptyList()

    /**
     * Inicia la observación de sesiones del usuario autenticado. Debe llamarse una vez al entrar a
     * ChatPage.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadSessions(context: Context) {
        val userId = AuthPreferences.getUserId(context)
        if (userId == -1) {
            _errorMessage.value = "No hay sesión activa."
            return
        }

        val db = AppDatabase.getInstance(context)

        viewModelScope.launch {
            db.chatSessionDao()
                    .getSessionsByUser(userId)
                    .catch { e ->
                        _errorMessage.value = e.message ?: "Error al cargar conversaciones."
                    }
                    .collect { list ->
                        rawSessions = list
                        applyFilter(_searchQuery.value)
                        loadLastMessagePreviews(context, list.map { it.id })
                    }
        }

        // Reaplica el filtro cada vez que cambia el texto de búsqueda
        viewModelScope.launch { _searchQuery.collect { query -> applyFilter(query) } }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /** Long press sobre un item abre el diálogo de renombrar. */
    fun onSessionLongPress(session: ChatSession) {
        _selectedSession.value = session
    }

    fun dismissRenameDialog() {
        _selectedSession.value = null
    }

    /** Persiste el nuevo nombre y cierra el diálogo. */
    fun renameSession(context: Context, session: ChatSession, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            try {
                val db = AppDatabase.getInstance(context)
                withContext(Dispatchers.IO) {
                    db.chatSessionDao()
                            .update(
                                    session.copy(
                                            title = newTitle.trim(),
                                            updatedAt = System.currentTimeMillis()
                                    )
                            )
                }
                _selectedSession.value = null
            } catch (e: Exception) {
                _errorMessage.value = "No se pudo renombrar la conversación."
            }
        }
    }

    fun deleteSession(context: Context, session: ChatSession) {
        viewModelScope.launch {
            try {
                val db = AppDatabase.getInstance(context)
                withContext(Dispatchers.IO) { db.chatSessionDao().deleteById(session.id) }
            } catch (e: Exception) {
                _errorMessage.value = "No se pudo eliminar la conversación."
            }
        }
    }

    /**
     * Crea una nueva sesión con título por defecto y emite su id para que NavGraph navegue a
     * ChatScreen.
     */
    fun createNewSession(context: Context) {
        val userId = AuthPreferences.getUserId(context)
        if (userId == -1) {
            _errorMessage.value = "No hay sesión activa."
            return
        }
        viewModelScope.launch {
            try {
                val db = AppDatabase.getInstance(context)
                val newSession =
                        ChatSession(
                                userId = userId,
                                title = "Nueva consulta",
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                        )
                val id = withContext(Dispatchers.IO) { db.chatSessionDao().insert(newSession) }
                _newSessionId.value = id.toInt()
            } catch (e: Exception) {
                _errorMessage.value = "No se pudo crear la conversación."
            }
        }
    }

    /** Debe llamarse desde la UI justo después de navegar a ChatScreen. */
    fun onNavigationHandled() {
        _newSessionId.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private suspend fun loadLastMessagePreviews(context: Context, sessionIds: List<Int>) {
        val db = AppDatabase.getInstance(context)
        val previews = mutableMapOf<Int, String>()
        withContext(Dispatchers.IO) {
            sessionIds.forEach { sessionId ->
                val last = db.chatMessageDao().getLastMessageBySession(sessionId)
                previews[sessionId] =
                        when {
                            last == null -> "¡Arro está aquí para ayudarte!"
                            last.length <= 40 -> last
                            else -> last.take(40) + "…"
                        }
            }
        }
        _lastMessagePreviews.value = previews
    }

    private fun applyFilter(query: String) {
        _sessions.value =
                if (query.isBlank()) {
                    rawSessions
                } else {
                    rawSessions.filter { it.title.contains(query, ignoreCase = true) }
                }
    }
}
