package com.clinic.dentalapp.data.dao

import androidx.room.*
import com.clinic.dentalapp.data.entity.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun getAll(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE patientId = :patientId ORDER BY date DESC")
    fun getByPatient(patientId: Long): Flow<List<Payment>>

    @Query("""
        SELECT patientId, patientName,
        SUM(CASE WHEN type = 'مستحق' THEN amount ELSE -amount END) as balance
        FROM payments WHERE currency = :currency
        GROUP BY patientId
        HAVING balance > 0
    """)
    fun getOutstandingBalances(currency: String): Flow<List<PatientBalance>>

    @Insert
    suspend fun insert(payment: Payment): Long

    @Delete
    suspend fun delete(payment: Payment)
}

data class PatientBalance(
    val patientId: Long,
    val patientName: String,
    val balance: Double
)
