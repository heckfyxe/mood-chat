package com.heckfyxe.moodchat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.heckfyxe.moodchat.model.*

@Database(
        entities = [Conversation::class, User::class, Message::class, Group::class, Attachment::class, Photo::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var database: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (database == null)
                database = Room
                    .databaseBuilder(context, AppDatabase::class.java, "AppDatabase")
                    .build()
            return database!!
        }
    }

    abstract fun getConversationDao(): ConversationDao

    abstract fun getUserDao(): UserDao

    abstract fun getMessageDao(): MessageDao

    abstract fun getGroupDao(): GroupDao
}