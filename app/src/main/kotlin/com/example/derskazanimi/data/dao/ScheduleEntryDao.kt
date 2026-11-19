package com.example.derskazanimi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.derskazanimi.data.entity.ScheduleEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleEntryDao {

    @Insert
    suspend fun insert(scheduleEntry: ScheduleEntry): Long

    @Update
    suspend fun update(scheduleEntry: ScheduleEntry)

    @Delete
    suspend fun delete(scheduleEntry: ScheduleEntry)

    @Query("SELECT * FROM schedule_entries WHERE id = :id")
    fun getScheduleEntryById(id: Int): Flow<ScheduleEntry?>

    @Query("SELECT * FROM schedule_entries WHERE dayOfWeek = :dayOfWeek ORDER BY timeSlotId ASC")
    fun getScheduleByDay(dayOfWeek: Int): Flow<List<ScheduleEntry>>

    @Query("SELECT * FROM schedule_entries WHERE className = :className ORDER BY dayOfWeek, timeSlotId")
    fun getScheduleByClassName(className: String): Flow<List<ScheduleEntry>>

    @Query("SELECT * FROM schedule_entries ORDER BY dayOfWeek, timeSlotId ASC")
    fun getAllScheduleEntries(): Flow<List<ScheduleEntry>>

    @Query("SELECT * FROM schedule_entries WHERE dayOfWeek = :dayOfWeek AND timeSlotId = :timeSlotId")
    fun getScheduleByDayAndTimeSlot(dayOfWeek: Int, timeSlotId: Int): Flow<List<ScheduleEntry>>

    @Query("DELETE FROM schedule_entries")
    suspend fun deleteAllScheduleEntries()
}
