package com.upb.obsia.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE celular = :celular LIMIT 1")
    suspend fun getByCelular(celular: String): User?
}
