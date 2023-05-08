package com.ins.quokkamvp.logger

import android.content.Context
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Query("SELECT * FROM logs ORDER BY created_at DESC")
    fun getAll(): Flow<List<LogEntity>>

    @Insert
    suspend fun insertAll(vararg log: LogEntity)

    @Query("DELETE FROM logs")
    suspend fun deleteAll()


    companion object {
        private var instance: LogDao? = null

        fun getInstance(context: Context): LogDao {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database",
                ).build().logDao().also {
                    instance = it
                }
            }
        }
    }
}
