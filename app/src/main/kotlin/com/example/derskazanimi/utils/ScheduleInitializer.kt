package com.example.derskazanimi.utils

import com.example.derskazanimi.data.entity.ScheduleEntry
import com.example.derskazanimi.data.entity.TimeSlot

object ScheduleInitializer {

    fun getTimeSlots(): List<TimeSlot> {
        return listOf(
            TimeSlot(id = 1, slotNumber = 1, startTime = "08:20", endTime = "09:00"),
            TimeSlot(id = 2, slotNumber = 2, startTime = "09:10", endTime = "09:50"),
            TimeSlot(id = 3, slotNumber = 3, startTime = "10:00", endTime = "10:40"),
            TimeSlot(id = 4, slotNumber = 4, startTime = "10:50", endTime = "11:30"),
            TimeSlot(id = 5, slotNumber = 5, startTime = "11:40", endTime = "12:20"),
            TimeSlot(id = 6, slotNumber = 6, startTime = "12:30", endTime = "13:10"),
            TimeSlot(id = 7, slotNumber = 7, startTime = "13:50", endTime = "14:30"),
            TimeSlot(id = 8, slotNumber = 8, startTime = "14:40", endTime = "15:20"),
            TimeSlot(id = 9, slotNumber = 9, startTime = "15:30", endTime = "16:10"),
            TimeSlot(id = 10, slotNumber = 10, startTime = "16:20", endTime = "17:00"),
            TimeSlot(id = 11, slotNumber = 11, startTime = "17:00", endTime = "17:40")
        )
    }

    fun getScheduleEntries(): List<ScheduleEntry> {
        return listOf(
            // Pazartesi (Monday = 2)
            ScheduleEntry(dayOfWeek = 2, timeSlotId = 1, className = "9-C"),
            ScheduleEntry(dayOfWeek = 2, timeSlotId = 2, className = "9-C"),
            ScheduleEntry(dayOfWeek = 2, timeSlotId = 5, className = "12-B"),
            ScheduleEntry(dayOfWeek = 2, timeSlotId = 6, className = "12-B"),

            // Salı (Tuesday = 3)
            ScheduleEntry(dayOfWeek = 3, timeSlotId = 3, className = "10-A"),
            ScheduleEntry(dayOfWeek = 3, timeSlotId = 4, className = "10-A"),
            ScheduleEntry(dayOfWeek = 3, timeSlotId = 7, className = "11-A"),
            ScheduleEntry(dayOfWeek = 3, timeSlotId = 8, className = "11-A"),

            // Çarşamba (Wednesday = 4)
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 2, className = "HZ-A"),
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 3, className = "HZ-A"),
            ScheduleEntry(dayOfWeek = 4, timeSlotId = 6, className = "12-B"),

            // Perşembe (Thursday = 5)
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 1, className = "12-B"),
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 4, className = "9-C"),
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 5, className = "HZ-B"),
            ScheduleEntry(dayOfWeek = 5, timeSlotId = 8, className = "11-A"),

            // Cuma (Friday = 6)
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 2, className = "10-A"),
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 3, className = "10-A"),
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 5, className = "HZ-C"),
            ScheduleEntry(dayOfWeek = 6, timeSlotId = 9, className = "9-C"),

            // Cumartesi (Saturday = 7)
            ScheduleEntry(dayOfWeek = 7, timeSlotId = 1, className = "11-A"),
            ScheduleEntry(dayOfWeek = 7, timeSlotId = 3, className = "12-B"),
            ScheduleEntry(dayOfWeek = 7, timeSlotId = 6, className = "HZ-A")
        )
    }
}
