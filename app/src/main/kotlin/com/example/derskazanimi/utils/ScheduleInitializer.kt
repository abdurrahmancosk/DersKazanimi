package com.example.derskazanimi.utils

import com.example.derskazanimi.data.entity.ScheduleEntry
import com.example.derskazanimi.data.entity.TimeSlot

// Ders Programı Başlangıç Tarihi: 23/11/2026 itibarıyla geçerli program
object ScheduleInitializer {

    fun getTimeSlots(): List<TimeSlot> {
        return listOf(
            TimeSlot(id = 1, slotNumber = 1, startTime = "08:20", endTime = "09:00"),
            TimeSlot(id = 2, slotNumber = 2, startTime = "09:10", endTime = "09:50"),
            TimeSlot(id = 3, slotNumber = 3, startTime = "10:00", endTime = "10:40"),
            TimeSlot(id = 4, slotNumber = 4, startTime = "10:50", endTime = "11:30"),
            TimeSlot(id = 5, slotNumber = 5, startTime = "11:40", endTime = "12:20"),
            TimeSlot(id = 6, slotNumber = 6, startTime = "13:10", endTime = "13:50"),
            TimeSlot(id = 7, slotNumber = 7, startTime = "14:00", endTime = "14:40"),
            TimeSlot(id = 8, slotNumber = 8, startTime = "14:50", endTime = "15:30"),
            TimeSlot(id = 10, slotNumber = 10, startTime = "15:40", endTime = "16:20"),
            TimeSlot(id = 11, slotNumber = 11, startTime = "16:20", endTime = "17:00")
        )
    }

    // dayOfWeek: Calendar.DAY_OF_WEEK convention -> Pazartesi=2, Salı=3, Çarşamba=4, Perşembe=5, Cuma=6, Cumartesi=7
    fun getScheduleEntries(): List<ScheduleEntry> {
        return listOf(
            // Pazartesi
            ScheduleEntry(dayOfWeek = 2, timeSlotId = 1, className = "9-C"),
            ScheduleEntry(dayOfWeek = 2, timeSlotId = 2, className = "9-C"),
            ScheduleEntry(dayOfWeek = 2, timeSlotId = 5, className = "12-B"),
            ScheduleEntry(dayOfWeek = 2, timeSlotId = 6, className = "12-B"),
            ScheduleEntry(dayOfWeek = 2, timeSlotId = 8, className = "HZ-C"),

            // Salı - ders yok

            // Çarşamba
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 2, className = "11-A"),
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 3, className = "HZ-A"),
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 4, className = "HZ-A"),
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 5, className = "HZ-B"),
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 6, className = "HZ-B"),
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 7, className = "12-B"),
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 8, className = "12-B"),

            // Perşembe
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 3, className = "12-B"),
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 4, className = "12-B"),
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 5, className = "9-C"),
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 6, className = "9-C"),
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 7, className = "HZ-C"),
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 8, className = "HZ-C"),

            // Cuma
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 3, className = "HZ-B"),
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 4, className = "9-C"),
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 5, className = "9-C"),
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 6, className = "HZ-A"),
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 7, className = "12-B"),
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 8, className = "12-B")

            // Cumartesi - ders yok
        )
    }
}
