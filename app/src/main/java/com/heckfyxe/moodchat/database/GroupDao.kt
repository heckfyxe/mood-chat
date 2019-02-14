package com.heckfyxe.moodchat.database

import androidx.room.Dao
import androidx.room.Query
import com.heckfyxe.moodchat.model.Group

@Dao
interface GroupDao : BaseDao<Group> {
    @Query("SELECT * FROM `group` WHERE id = :id LIMIT 1")
    suspend fun getGroupById(id: Int): Group?
}