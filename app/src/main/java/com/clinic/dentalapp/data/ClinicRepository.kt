package com.clinic.dentalapp.data

import android.content.Context
import com.clinic.dentalapp.data.entity.User

class ClinicRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)

    val patientDao = db.patientDao()
    val appointmentDao = db.appointmentDao()
    val toothDao = db.toothDao()
    val paymentDao = db.paymentDao()
    val inventoryDao = db.inventoryDao()
    val imageDao = db.imageDao()
    val settingsDao = db.settingsDao()
    val userDao = db.userDao()

    suspend fun ensureDefaultUsers() {
        if (userDao.count() == 0) {
            userDao.insert(User(username = "admin", password = "admin123", role = "admin"))
            userDao.insert(User(username = "employee", password = "emp123", role = "employee"))
        }
    }

    suspend fun nextFileNumber(): String {
        val count = patientDao.count() + 1
        return "P-" + count.toString().padStart(4, '0')
    }
}
