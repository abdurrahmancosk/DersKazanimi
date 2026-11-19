package com.example.derskazanimi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_slots")
data class TimeSlot(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val slotNumber: Int,
    val startTime: String, // Format: "HH:mm"
    val endTime: String    // Format: "HH:mm"
)
