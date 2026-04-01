package com.upb.obsia.di

import android.content.Context
import com.upb.obsia.data.AppDatabase
import com.upb.obsia.data.ChatMessageDao
import com.upb.obsia.data.ChatSessionDao
import com.upb.obsia.data.UserDao
import com.upb.obsia.data.repository.ChatRepositoryImpl
import com.upb.obsia.data.repository.EngineRepositoryImpl
import com.upb.obsia.domain.repository.ChatRepository
import com.upb.obsia.domain.repository.EngineRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
            AppDatabase.getInstance(context)

    @Provides fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()

    @Provides fun provideChatSessionDao(db: AppDatabase): ChatSessionDao = db.chatSessionDao()

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEngineRepository(impl: EngineRepositoryImpl): EngineRepository

    @Binds @Singleton abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
