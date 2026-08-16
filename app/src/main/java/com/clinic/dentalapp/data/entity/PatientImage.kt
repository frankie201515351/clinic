package com.clinic.dentalapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_images")
data class PatientImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Long,
    val uri: String,
    val note: String = "",
    val date: String
)
