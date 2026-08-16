package com.clinic.dentalapp.data.dao

import androidx.room.*
import com.clinic.dentalapp.data.entity.ToothRecord
import com.clinic.dentalapp.data.entity.TreatmentSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ToothDao {
    @Query("SELECT * FROM tooth_records WHERE patientId = :patientId")
    fun getByPatient(patientId: Long): Flow<List<ToothRecord>>

    @Query("SELECT * FROM tooth_records WHERE patientId = :patientId AND toothNumber = :toothNumber LIMIT 1")
    suspend fun getTooth(patientId: Long, toothNumber: Int): ToothRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: ToothRecord): Long

    @Query("SELECT * FROM treatment_sessions WHERE patientId = :patientId ORDER BY date DESC")
    fun getSessions(patientId: Long): Flow<List<TreatmentSession>>

    @Insert
    suspend fun insertSession(session: TreatmentSession): Long

    @Delete
    suspend fun deleteSession(session: TreatmentSession)
}
