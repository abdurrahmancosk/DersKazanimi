package com.example.derskazanimi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.derskazanimi.data.entity.DutyDate
import kotlinx.coroutines.flow.Flow

@Dao
interface DutyDateDao {

    @Insert
    suspend fun insert(dutyDate: DutyDate): Long

    @Query("SELECT * FROM duty_dates ORDER BY date ASC")
    fun getAllDutyDates(): Flow<List<DutyDate>>

    @Query("SELECT * FROM duty_dates WHERE date = :date LIMIT 1")
    fun getDutyDateForDate(date: String): Flow<DutyDate?>

    @Query("DELETE FROM duty_dates")
    suspend fun deleteAllDutyDates()
}
