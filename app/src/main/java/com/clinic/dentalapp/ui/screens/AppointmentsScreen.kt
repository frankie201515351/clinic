package com.clinic.dentalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clinic.dentalapp.data.ClinicRepository
import com.clinic.dentalapp.data.entity.Appointment
import com.clinic.dentalapp.data.entity.Patient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppointmentsScreen(repository: ClinicRepository) {
    val scope = rememberCoroutineScope()
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var selectedDate by remember { mutableStateOf(dateFormat.format(Date())) }
    var showAdd by remember { mutableStateOf(false) }

    val appointments by repository.appointmentDao.getByDate(selectedDate).collectAsState(initial = emptyList())
    val patients by repository.patientDao.getAll().collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, contentDescription = "إضافة موعد") }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("المواعيد", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Row {
                QuickDateButton("اليوم", 0, dateFormat) { selectedDate = it }
                Spacer(Modifier.width(8.dp))
                QuickDateButton("بكرا", 1, dateFormat) { selectedDate = it }
                Spacer(Modifier.width(8.dp))
                QuickDateButton("بعد أسبوع", 7, dateFormat) { selectedDate = it }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { selectedDate = it },
                label = { Text("التاريخ (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            if (appointments.isEmpty()) {
                Text("لا يوجد مواعيد بهذا التاريخ", color = MaterialTheme.colorScheme.secondary)
            }

            LazyColumn {
                items(appointments) { appt ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(appt.patientName, fontWeight = FontWeight.Bold)
                                Text("${appt.time} • ${appt.durationMinutes} دقيقة", fontSize = 13.sp)
                            }
                            IconButton(onClick = {
                                scope.launch { repository.appointmentDao.delete(appt) }
                            }) { Icon(Icons.Default.Delete, contentDescription = "حذف") }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddAppointmentDialog(
            patients = patients,
            defaultDate = selectedDate,
            onDismiss = { showAdd = false },
            onSave = { patient, date, time, duration ->
                scope.launch {
                    repository.appointmentDao.insert(
                        Appointment(
                            patientId = patient.id,
                            patientName = patient.fullName,
                            date = date,
                            time = time,
                            durationMinutes = duration
                        )
                    )
                    showAdd = false
                }
            }
        )
    }
}

@Composable
fun QuickDateButton(label: String, daysFromNow: Int, dateFormat: SimpleDateFormat, onSelect: (String) -> Unit) {
    OutlinedButton(onClick = {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysFromNow)
        onSelect(dateFormat.format(cal.time))
    }) { Text(label) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentDialog(
    patients: List<Patient>,
    defaultDate: String,
    onDismiss: () -> Unit,
    onSave: (Patient, String, String, Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var date by remember { mutableStateOf(defaultDate) }
    var time by remember { mutableStateOf("10:00") }
    var duration by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة موعد") },
        text = {
            Column {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedPatient?.fullName ?: "اختر المريض",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        patients.forEach { p ->
                            DropdownMenuItem(text = { Text(p.fullName) }, onClick = {
                                selectedPatient = p
                                expanded = false
                            })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("التاريخ (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("الوقت (HH:mm)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("المدة (دقيقة)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                selectedPatient?.let { p ->
                    onSave(p, date, time, duration.toIntOrNull() ?: 30)
                }
            }) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
