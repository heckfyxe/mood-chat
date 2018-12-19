package com.heckfyxe.moodchat

import com.heckfyxe.moodchat.database.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val koinModule = module {
    single { AppDatabase.getInstance(androidApplication()) }
    factory { get<AppDatabase>().getConversationDao() }
    factory { get<AppDatabase>().getMessageDao() }
    factory { get<AppDatabase>().getUserDao() }
    factory { get<AppDatabase>().getGroupDao() }
    viewModel { ConversationsViewModel(get(), get(), get(), get()) }
}