package com.heckfyxe.moodchat.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T : Any> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: T?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: List<T?>)

    @Update
    suspend fun update(vararg data: T?)

    @Update
    suspend fun update(data: List<T?>)

    @Delete
    suspend fun delete(vararg data: T?)

    @Delete
    suspend fun delete(data: List<T?>)
}