package com.upb.obsia.data.repository

import com.upb.obsia.data.ChatMessage
import com.upb.obsia.data.ChatMessageDao
import com.upb.obsia.data.ChatSessionDao
import com.upb.obsia.domain.repository.ChatRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class ChatRepositoryImpl
@Inject
constructor(
        private val chatMessageDao: ChatMessageDao,
        private val chatSessionDao: ChatSessionDao
) : ChatRepository {

    override suspend fun getSessionName(sessionId: Int): String? =
            withContext(Dispatchers.IO) { chatSessionDao.getById(sessionId)?.title }

    override suspend fun getMessages(sessionId: Int): List<ChatMessage> =
            withContext(Dispatchers.IO) { chatMessageDao.getMessagesBySessionOnce(sessionId) }

    override suspend fun saveMessage(message: ChatMessage): Long =
            withContext(Dispatchers.IO) { chatMessageDao.insert(message) }

    override suspend fun touchSession(sessionId: Int) {
        withContext(Dispatchers.IO) {
            chatSessionDao.getById(sessionId)?.let { session ->
                chatSessionDao.update(session.copy(updatedAt = System.currentTimeMillis()))
            }
        }
    }
}
