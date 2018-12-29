package com.heckfyxe.moodchat.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heckfyxe.moodchat.model.Group

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg groups: Group)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(groups: List<Group>)

    @Query("SELECT * FROM `group` WHERE id = :id LIMIT 1")
    fun getGroupById(id: Int): Group
}