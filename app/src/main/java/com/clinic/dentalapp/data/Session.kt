package com.clinic.dentalapp.data

object Session {
    var username: String = ""
    var role: String = "" // "admin" أو "employee"

    val isAdmin: Boolean
        get() = role == "admin"

    fun logout() {
        username = ""
        role = ""
    }
}
