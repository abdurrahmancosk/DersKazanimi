package com.example.derskazanimi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.derskazanimi.data.entity.LearningOutcome
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningOutcomeDao {

    @Insert
    suspend fun insert(learningOutcome: LearningOutcome): Long

    @Update
    suspend fun update(learningOutcome: LearningOutcome)

    @Delete
    suspend fun delete(learningOutcome: LearningOutcome)

    @Query("SELECT * FROM learning_outcomes WHERE id = :id")
    fun getLearningOutcomeById(id: Int): Flow<LearningOutcome?>

    @Query("SELECT * FROM learning_outcomes WHERE className = :className ORDER BY weeklyPlanId DESC")
    fun getLearningOutcomesByClassName(className: String): Flow<List<LearningOutcome>>

    @Query("SELECT * FROM learning_outcomes WHERE weeklyPlanId = :weeklyPlanId ORDER BY className")
    fun getLearningOutcomesByWeeklyPlan(weeklyPlanId: Int): Flow<List<LearningOutcome>>

    @Query("SELECT * FROM learning_outcomes WHERE className = :className AND weeklyPlanId = :weeklyPlanId")
    fun getLearningOutcomeByClassAndWeek(className: String, weeklyPlanId: Int): Flow<List<LearningOutcome>>

    @Query("SELECT * FROM learning_outcomes ORDER BY weeklyPlanId DESC, className")
    fun getAllLearningOutcomes(): Flow<List<LearningOutcome>>

    @Query("DELETE FROM learning_outcomes")
    suspend fun deleteAllLearningOutcomes()
}
