package com.example.derskazanimi

import android.app.Application
import com.example.derskazanimi.data.AppDatabase
import com.example.derskazanimi.data.repository.DersRepository
import com.example.derskazanimi.utils.NotificationHelper
import com.example.derskazanimi.utils.ScheduleInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DersKazanimiApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createNotificationChannel(this)

        applicationScope.launch {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = DersRepository(database)

            if (repository.getAllTimeSlots().first().isEmpty()) {
                ScheduleInitializer.getTimeSlots().forEach { repository.insertTimeSlot(it) }
            }

            if (repository.getAllScheduleEntries().first().isEmpty()) {
                ScheduleInitializer.getScheduleEntries().forEach { repository.insertScheduleEntry(it) }
            }
        }
    }
}
