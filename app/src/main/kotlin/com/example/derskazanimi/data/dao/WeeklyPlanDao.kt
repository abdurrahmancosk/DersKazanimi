package com.example.derskazanimi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.derskazanimi.data.entity.WeeklyPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyPlanDao {

    @Insert
    suspend fun insert(weeklyPlan: WeeklyPlan): Long

    @Update
    suspend fun update(weeklyPlan: WeeklyPlan)

    @Delete
    suspend fun delete(weeklyPlan: WeeklyPlan)

    @Query("SELECT * FROM weekly_plans WHERE id = :id")
    fun getWeeklyPlanById(id: Int): Flow<WeeklyPlan?>

    @Query("SELECT * FROM weekly_plans WHERE weekNumber = :weekNumber")
    fun getWeeklyPlanByWeekNumber(weekNumber: Int): Flow<WeeklyPlan?>

    @Query("SELECT * FROM weekly_plans WHERE :date BETWEEN startDate AND endDate LIMIT 1")
    fun getWeeklyPlanForDate(date: String): Flow<WeeklyPlan?>

    @Query("SELECT * FROM weekly_plans ORDER BY weekNumber ASC")
    fun getAllWeeklyPlans(): Flow<List<WeeklyPlan>>

    @Query("DELETE FROM weekly_plans")
    suspend fun deleteAllWeeklyPlans()
}
