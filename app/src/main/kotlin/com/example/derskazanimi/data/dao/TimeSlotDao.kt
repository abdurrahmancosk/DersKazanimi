package com.example.derskazanimi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.derskazanimi.data.entity.TimeSlot
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeSlotDao {

    @Insert
    suspend fun insert(timeSlot: TimeSlot): Long

    @Update
    suspend fun update(timeSlot: TimeSlot)

    @Delete
    suspend fun delete(timeSlot: TimeSlot)

    @Query("SELECT * FROM time_slots WHERE id = :id")
    fun getTimeSlotById(id: Int): Flow<TimeSlot?>

    @Query("SELECT * FROM time_slots ORDER BY slotNumber ASC")
    fun getAllTimeSlots(): Flow<List<TimeSlot>>

    @Query("DELETE FROM time_slots")
    suspend fun deleteAllTimeSlots()
}
