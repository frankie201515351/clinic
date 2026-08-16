package com.clinic.dentalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.clinic.dentalapp.data.dao.*
import com.clinic.dentalapp.data.entity.*

@Database(
    entities = [
        User::class,
        Patient::class,
        Appointment::class,
        ToothRecord::class,
        TreatmentSession::class,
        Payment::class,
        InventoryItem::class,
        PatientImage::class,
        ClinicSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun patientDao(): PatientDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun toothDao(): ToothDao
    abstract fun paymentDao(): PaymentDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun imageDao(): ImageDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dental_clinic.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
