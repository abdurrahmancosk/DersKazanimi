package com.example.derskazanimi.utils

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.derskazanimi.worker.LessonNotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object AlarmScheduler {

    fun scheduleAlarmNotifications(context: Context) {
        // Create periodic work request for daily 7:00 AM
        val lessonNotificationWork = PeriodicWorkRequestBuilder<LessonNotificationWorker>(
            15, // Repeat every 15 minutes (minimum allowed by WorkManager)
            TimeUnit.MINUTES
        )
            .addTag("lesson_notifications")
            .build()

        // Schedule the work
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "lesson_notification_work",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            lessonNotificationWork
        )
    }

    fun cancelAlarmNotifications(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("lesson_notifications")
    }

    fun isAlarmScheduled(context: Context): Boolean {
        return try {
            val workInfos = WorkManager.getInstance(context)
                .getWorkInfosByTag("lesson_notifications")
                .get()
            workInfos.isNotEmpty() && workInfos.any { !it.state.isFinished }
        } catch (e: Exception) {
            false
        }
    }
}
