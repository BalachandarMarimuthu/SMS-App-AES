package com.smsapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smsapplication.model.InboxMessage

@Dao
interface InboxDao {

    @Query("SELECT * FROM InboxMessage")
    fun getInboxMessages(): MutableList<InboxMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(inboxMessages: MutableList<InboxMessage>)

    @Query("DELETE FROM InboxMessage")
    fun deleteAll()
}
