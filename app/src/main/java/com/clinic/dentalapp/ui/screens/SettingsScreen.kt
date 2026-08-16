package com.clinic.dentalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clinic.dentalapp.data.ClinicRepository
import com.clinic.dentalapp.data.Session
import com.clinic.dentalapp.data.entity.ClinicSettings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(repository: ClinicRepository, onLogout: () -> Unit) {
    val scope = rememberCoroutineScope()
    val settings by repository.settingsDao.get().collectAsState(initial = null)

    var openTime by remember(settings) { mutableStateOf(settings?.openTime ?: "09:00") }
    var closeTime by remember(settings) { mutableStateOf(settings?.closeTime ?: "18:00") }
    var offDays by remember(settings) { mutableStateOf(settings?.weeklyOffDays ?: "الجمعة") }
    var exchangeRate by remember(settings) { mutableStateOf((settings?.exchangeRate ?: 15000.0).toString()) }
    var saved by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("الإعدادات", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Text("مسجل الدخول: ${Session.username} (${Session.role})", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(20.dp))

        if (Session.isAdmin) {
            Text("ساعات دوام العيادة", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = openTime, onValueChange = { openTime = it }, label = { Text("وقت البدء (HH:mm)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = closeTime, onValueChange = { closeTime = it }, label = { Text("وقت الانتهاء (HH:mm)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = offDays, onValueChange = { offDays = it }, label = { Text("أيام العطلة الأسبوعية") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = exchangeRate, onValueChange = { exchangeRate = it }, label = { Text("سعر الصرف (ليرة مقابل الدولار)") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        repository.settingsDao.save(
                            ClinicSettings(
                                openTime = openTime,
                                closeTime = closeTime,
                                weeklyOffDays = offDays,
                                exchangeRate = exchangeRate.toDoubleOrNull() ?: 15000.0
                            )
                        )
                        saved = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("حفظ الإعدادات") }

            if (saved) {
                Spacer(Modifier.height(8.dp))
                Text("تم الحفظ بنجاح", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(24.dp))
        }

        OutlinedButton(
            onClick = {
                Session.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("تسجيل الخروج") }
    }
}
