package com.heckfyxe.moodchat.database

import androidx.room.Dao
import androidx.room.Query
import com.heckfyxe.moodchat.model.User

@Dao
interface UserDao : BaseDao<User> {
    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?
}