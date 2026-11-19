package com.example.derskazanimi.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "learning_outcomes",
    foreignKeys = [
        ForeignKey(
            entity = WeeklyPlan::class,
            parentColumns = ["id"],
            childColumns = ["weeklyPlanId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LearningOutcome(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val className: String, // e.g., "9-C", "12-B", "HZ-A"
    val weeklyPlanId: Int, // Foreign key to WeeklyPlan
    val subject: String, // e.g., "Matematik", "Seçmeli Matematik"
    val kazanim: String // Learning outcome text
)
