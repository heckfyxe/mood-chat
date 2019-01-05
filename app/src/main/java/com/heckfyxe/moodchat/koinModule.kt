package com.heckfyxe.moodchat

import com.heckfyxe.moodchat.database.AppDatabase
import com.heckfyxe.moodchat.repository.ConversationRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val koinModule = module {
    // Database
    single { AppDatabase.getInstance(androidApplication()) }

    // Database Dao
    factory { get<AppDatabase>().getConversationDao() }
    factory { get<AppDatabase>().getMessageDao() }
    factory { get<AppDatabase>().getUserDao() }
    factory { get<AppDatabase>().getGroupDao() }

    // Repositories
    factory { ConversationRepository() }

    // ViewModels
    viewModel { ConversationsViewModel(get()) }
    viewModel { MessagesViewModel() }
}