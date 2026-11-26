package com.example.derskazanimi.data.repository

import com.example.derskazanimi.data.AppDatabase
import com.example.derskazanimi.data.entity.DutyDate
import com.example.derskazanimi.data.entity.LearningOutcome
import com.example.derskazanimi.data.entity.ScheduleEntry
import com.example.derskazanimi.data.entity.TimeSlot
import com.example.derskazanimi.data.entity.WeeklyPlan
import kotlinx.coroutines.flow.Flow

class DersRepository(private val database: AppDatabase) {

    // TimeSlot operations
    suspend fun insertTimeSlot(timeSlot: TimeSlot): Long =
        database.timeSlotDao().insert(timeSlot)

    suspend fun updateTimeSlot(timeSlot: TimeSlot) =
        database.timeSlotDao().update(timeSlot)

    suspend fun deleteTimeSlot(timeSlot: TimeSlot) =
        database.timeSlotDao().delete(timeSlot)

    fun getTimeSlotById(id: Int): Flow<TimeSlot?> =
        database.timeSlotDao().getTimeSlotById(id)

    fun getAllTimeSlots(): Flow<List<TimeSlot>> =
        database.timeSlotDao().getAllTimeSlots()

    suspend fun deleteAllTimeSlots() =
        database.timeSlotDao().deleteAllTimeSlots()

    // ScheduleEntry operations
    suspend fun insertScheduleEntry(scheduleEntry: ScheduleEntry): Long =
        database.scheduleEntryDao().insert(scheduleEntry)

    suspend fun updateScheduleEntry(scheduleEntry: ScheduleEntry) =
        database.scheduleEntryDao().update(scheduleEntry)

    suspend fun deleteScheduleEntry(scheduleEntry: ScheduleEntry) =
        database.scheduleEntryDao().delete(scheduleEntry)

    fun getScheduleByDay(dayOfWeek: Int): Flow<List<ScheduleEntry>> =
        database.scheduleEntryDao().getScheduleByDay(dayOfWeek)

    fun getScheduleByClassName(className: String): Flow<List<ScheduleEntry>> =
        database.scheduleEntryDao().getScheduleByClassName(className)

    fun getAllScheduleEntries(): Flow<List<ScheduleEntry>> =
        database.scheduleEntryDao().getAllScheduleEntries()

    fun getScheduleByDayAndTimeSlot(dayOfWeek: Int, timeSlotId: Int): Flow<List<ScheduleEntry>> =
        database.scheduleEntryDao().getScheduleByDayAndTimeSlot(dayOfWeek, timeSlotId)

    suspend fun deleteAllScheduleEntries() =
        database.scheduleEntryDao().deleteAllScheduleEntries()

    // WeeklyPlan operations
    suspend fun insertWeeklyPlan(weeklyPlan: WeeklyPlan): Long =
        database.weeklyPlanDao().insert(weeklyPlan)

    suspend fun updateWeeklyPlan(weeklyPlan: WeeklyPlan) =
        database.weeklyPlanDao().update(weeklyPlan)

    suspend fun deleteWeeklyPlan(weeklyPlan: WeeklyPlan) =
        database.weeklyPlanDao().delete(weeklyPlan)

    fun getWeeklyPlanById(id: Int): Flow<WeeklyPlan?> =
        database.weeklyPlanDao().getWeeklyPlanById(id)

    fun getWeeklyPlanByWeekNumber(weekNumber: Int): Flow<WeeklyPlan?> =
        database.weeklyPlanDao().getWeeklyPlanByWeekNumber(weekNumber)

    fun getWeeklyPlanForDate(date: String): Flow<WeeklyPlan?> =
        database.weeklyPlanDao().getWeeklyPlanForDate(date)

    fun getAllWeeklyPlans(): Flow<List<WeeklyPlan>> =
        database.weeklyPlanDao().getAllWeeklyPlans()

    suspend fun deleteAllWeeklyPlans() =
        database.weeklyPlanDao().deleteAllWeeklyPlans()

    // LearningOutcome operations
    suspend fun insertLearningOutcome(learningOutcome: LearningOutcome): Long =
        database.learningOutcomeDao().insert(learningOutcome)

    suspend fun updateLearningOutcome(learningOutcome: LearningOutcome) =
        database.learningOutcomeDao().update(learningOutcome)

    suspend fun deleteLearningOutcome(learningOutcome: LearningOutcome) =
        database.learningOutcomeDao().delete(learningOutcome)

    fun getLearningOutcomesByClassName(className: String): Flow<List<LearningOutcome>> =
        database.learningOutcomeDao().getLearningOutcomesByClassName(className)

    fun getLearningOutcomesByWeeklyPlan(weeklyPlanId: Int): Flow<List<LearningOutcome>> =
        database.learningOutcomeDao().getLearningOutcomesByWeeklyPlan(weeklyPlanId)

    fun getLearningOutcomeByClassAndWeek(className: String, weeklyPlanId: Int): Flow<List<LearningOutcome>> =
        database.learningOutcomeDao().getLearningOutcomeByClassAndWeek(className, weeklyPlanId)

    fun getAllLearningOutcomes(): Flow<List<LearningOutcome>> =
        database.learningOutcomeDao().getAllLearningOutcomes()

    suspend fun deleteAllLearningOutcomes() =
        database.learningOutcomeDao().deleteAllLearningOutcomes()

    // DutyDate operations
    suspend fun insertDutyDate(dutyDate: DutyDate): Long =
        database.dutyDateDao().insert(dutyDate)

    fun getAllDutyDates(): Flow<List<DutyDate>> =
        database.dutyDateDao().getAllDutyDates()

    fun getDutyDateForDate(date: String): Flow<DutyDate?> =
        database.dutyDateDao().getDutyDateForDate(date)

    suspend fun deleteAllDutyDates() =
        database.dutyDateDao().deleteAllDutyDates()
}
