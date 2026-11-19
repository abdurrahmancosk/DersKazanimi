package com.example.derskazanimi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.derskazanimi.data.dao.LearningOutcomeDao
import com.example.derskazanimi.data.dao.ScheduleEntryDao
import com.example.derskazanimi.data.dao.TimeSlotDao
import com.example.derskazanimi.data.dao.WeeklyPlanDao
import com.example.derskazanimi.data.entity.LearningOutcome
import com.example.derskazanimi.data.entity.ScheduleEntry
import com.example.derskazanimi.data.entity.TimeSlot
import com.example.derskazanimi.data.entity.WeeklyPlan

@Database(
    entities = [
        TimeSlot::class,
        ScheduleEntry::class,
        WeeklyPlan::class,
        LearningOutcome::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun timeSlotDao(): TimeSlotDao
    abstract fun scheduleEntryDao(): ScheduleEntryDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao
    abstract fun learningOutcomeDao(): LearningOutcomeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "derskazanimi_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
