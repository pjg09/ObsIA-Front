// Ruta: app/src/main/java/com/upb/obsia/data/ChatSessionDao.kt

package com.upb.obsia.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {

    @Insert suspend fun insert(session: ChatSession): Long

    @Update suspend fun update(session: ChatSession)

    @Query("SELECT * FROM chat_sessions WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getSessionsByUser(userId: Int): Flow<List<ChatSession>>

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getById(sessionId: Int): ChatSession?

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId") suspend fun deleteById(sessionId: Int)
}
