package com.clinic.dentalapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "treatment_sessions")
data class TreatmentSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Long,
    val toothNumber: Int, // 0 يعني غير مرتبطة بسن محدد
    val date: String,
    val description: String
)
