package com.clinic.dentalapp.data.dao

import androidx.room.*
import com.clinic.dentalapp.data.entity.ClinicSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM clinic_settings WHERE id = 1")
    fun get(): Flow<ClinicSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(settings: ClinicSettings)
}
