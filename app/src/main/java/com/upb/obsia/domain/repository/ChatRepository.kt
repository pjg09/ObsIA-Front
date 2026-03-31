package com.upb.obsia.domain.repository

import com.upb.obsia.data.ChatMessage

/**
 * Contrato de persistencia del chat. El ViewModel no conoce Room, DAOs ni AppDatabase — solo esta
 * interfaz.
 */
interface ChatRepository {

    /** Retorna el nombre de la sesión, o null si no existe. */
    suspend fun getSessionName(sessionId: Int): String?

    /** Carga el historial de mensajes de una sesión, ordenados por fecha. */
    suspend fun getMessages(sessionId: Int): List<ChatMessage>

    /** Persiste un mensaje y retorna su id generado. */
    suspend fun saveMessage(message: ChatMessage): Long

    /**
     * Actualiza el timestamp de última actividad de la sesión. Se llama después de cada mensaje
     * para mantener el orden en ChatList.
     */
    suspend fun touchSession(sessionId: Int)
}
