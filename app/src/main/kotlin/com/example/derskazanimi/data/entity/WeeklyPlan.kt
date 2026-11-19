package com.example.derskazanimi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_plans")
data class WeeklyPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weekNumber: Int,
    val startDate: String, // Format: "yyyy-MM-dd"
    val endDate: String    // Format: "yyyy-MM-dd"
)
