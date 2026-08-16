package com.clinic.dentalapp.data.dao

import androidx.room.*
import com.clinic.dentalapp.data.entity.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY fullName ASC")
    fun getAll(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE fullName LIKE '%' || :query || '%' ORDER BY fullName ASC")
    fun search(query: String): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE id = :id")
    suspend fun getById(id: Long): Patient?

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun count(): Int

    @Insert
    suspend fun insert(patient: Patient): Long

    @Update
    suspend fun update(patient: Patient)

    @Delete
    suspend fun delete(patient: Patient)
}
