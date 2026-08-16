package com.clinic.dentalapp.data.dao

import androidx.room.*
import com.clinic.dentalapp.data.entity.PatientImage
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM patient_images WHERE patientId = :patientId ORDER BY date DESC")
    fun getByPatient(patientId: Long): Flow<List<PatientImage>>

    @Insert
    suspend fun insert(image: PatientImage): Long

    @Delete
    suspend fun delete(image: PatientImage)
}
