package com.smsapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SentMessage(
    val number: String,
    val message: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
