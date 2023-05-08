package com.ins.quokkamvp.logger

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo("message") val message: String,
    @ColumnInfo("created_at") val createdAt: Long = System.currentTimeMillis(),
)
