package com.example.derskazanimi.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule_entries",
    foreignKeys = [
        ForeignKey(
            entity = TimeSlot::class,
            parentColumns = ["id"],
            childColumns = ["timeSlotId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ScheduleEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayOfWeek: Int, // Calendar.DAY_OF_WEEK convention: 1=Sunday, 2=Monday, ..., 7=Saturday
    val timeSlotId: Int, // Foreign key to TimeSlot
    val className: String // e.g., "9-C", "12-B", "HZ-A"
)
