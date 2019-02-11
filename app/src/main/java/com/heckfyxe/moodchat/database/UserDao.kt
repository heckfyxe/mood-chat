package com.heckfyxe.moodchat.database

import androidx.room.*
import com.heckfyxe.moodchat.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg users: User?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<User?>)

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?

    @Delete
    suspend fun deleteUsers(vararg users: User?)

    @Delete
    suspend fun deleteUsers(users: List<User?>)
}