package com.clinic.dentalapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Long,
    val patientName: String,
    val amount: Double,
    val currency: String, // "SYP" أو "USD"
    val type: String,     // "دفعة" أو "مستحق"
    val date: String,
    val note: String = ""
)
