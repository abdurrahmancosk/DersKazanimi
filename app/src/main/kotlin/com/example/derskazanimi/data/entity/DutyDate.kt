package com.example.derskazanimi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duty_dates")
data class DutyDate(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val note: String = "Pansiyon Nöbeti"
)
