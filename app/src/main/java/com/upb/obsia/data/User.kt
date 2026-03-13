package com.upb.obsia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val nombre: String,
        val email: String,
        val celular: String,
)
