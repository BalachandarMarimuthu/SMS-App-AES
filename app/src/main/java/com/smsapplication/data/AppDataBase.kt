package com.smsapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smsapplication.model.InboxMessage
import com.smsapplication.model.SentMessage


@Database(entities = [InboxMessage::class, SentMessage::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun inbox(): InboxDao
    abstract fun sentMessages(): SentMessageDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabaseClient(context: Context): AppDataBase {
            if (INSTANCE != null) return INSTANCE!!
            synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, AppDataBase::class.java, "LOGIN_DATABASE")
                    .fallbackToDestructiveMigration()
                    .build()
                return INSTANCE!!
            }
        }
    }
}
