package com.smsapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smsapplication.model.SentMessage

@Dao
interface SentMessageDao {

    @Query("SELECT * FROM SentMessage")
    fun getSentMessages(): MutableList<SentMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(inboxMessages: MutableList<SentMessage>)

    @Query("DELETE FROM SentMessage")
    fun deleteAll()
}
