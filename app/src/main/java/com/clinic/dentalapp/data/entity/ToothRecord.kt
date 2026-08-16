package com.clinic.dentalapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tooth_records")
data class ToothRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Long,
    val toothNumber: Int, // 1 - 32
    val status: String,   // مثال: سليم / تسوس / محشو / مقلوع / تاج
    val rootCanal: Boolean = false,
    val notes: String = ""
)
