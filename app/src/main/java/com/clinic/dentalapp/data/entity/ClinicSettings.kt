package com.clinic.dentalapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clinic_settings")
data class ClinicSettings(
    @PrimaryKey val id: Int = 1,
    val openTime: String = "09:00",
    val closeTime: String = "18:00",
    val weeklyOffDays: String = "الجمعة", // مفصولة بفاصلة إن وجد أكثر من يوم
    val exchangeRate: Double = 15000.0
)
