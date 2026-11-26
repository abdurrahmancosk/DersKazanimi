package com.example.derskazanimi.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.derskazanimi.R
import com.example.derskazanimi.data.AppDatabase
import com.example.derskazanimi.data.repository.DersRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LessonNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = DersRepository(database)

            // Get current time info
            val calendar = Calendar.getInstance()
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // Calendar.DAY_OF_WEEK: Sun=1..Sat=7

            // Check if there's a lesson within the notification window (150-40 minutes before)
            val notificationSent = checkAndNotifyUpcomingLessons(
                repository,
                currentTime,
                dayOfWeek
            )

            if (notificationSent) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun checkAndNotifyUpcomingLessons(
        repository: DersRepository,
        currentTime: String,
        dayOfWeek: Int
    ): Boolean {
        try {
            // Get all time slots
            val timeSlots = repository.getAllTimeSlots().first()
            val scheduleEntries = repository.getScheduleByDay(dayOfWeek).first()

            for (entry in scheduleEntries) {
                val timeSlot = timeSlots.find { it.id == entry.timeSlotId } ?: continue
                val lessonStartTime = timeSlot.startTime

                // Check if lesson is within 150-40 minutes window
                val minutesBeforeLesson = calculateMinutesBefore(currentTime, lessonStartTime)

                if (minutesBeforeLesson in 40..150) {
                    // Send notification
                    sendNotification(entry.className, lessonStartTime)
                    return true
                }
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun calculateMinutesBefore(currentTime: String, lessonStartTime: String): Int {
        return try {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            val currentCalendar = Calendar.getInstance().apply {
                val currentDate = format.parse(currentTime)
                set(Calendar.HOUR_OF_DAY, currentDate?.hours ?: 0)
                set(Calendar.MINUTE, currentDate?.minutes ?: 0)
            }

            val lessonCalendar = Calendar.getInstance().apply {
                val lessonDate = format.parse(lessonStartTime)
                set(Calendar.HOUR_OF_DAY, lessonDate?.hours ?: 0)
                set(Calendar.MINUTE, lessonDate?.minutes ?: 0)
            }

            val diffInMs = lessonCalendar.timeInMillis - currentCalendar.timeInMillis
            (diffInMs / (1000 * 60)).toInt()
        } catch (e: Exception) {
            0
        }
    }

    private fun sendNotification(className: String, lessonTime: String) {
        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Yaklaşan Ders")
            .setContentText("$className dersine $lessonTime'de başlanacak")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "lesson_notifications"
        const val NOTIFICATION_ID = 1001
    }
}
