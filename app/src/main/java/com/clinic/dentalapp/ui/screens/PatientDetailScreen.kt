package com.clinic.dentalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clinic.dentalapp.data.ClinicRepository
import com.clinic.dentalapp.data.Session
import com.clinic.dentalapp.data.entity.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(repository: ClinicRepository, patientId: Long, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var patient by remember { mutableStateOf<Patient?>(null) }
    var tabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(patientId) {
        patient = repository.patientDao.getById(patientId)
    }

    val tabs = if (Session.isAdmin)
        listOf("البيانات", "الأسنان", "الصور", "الحساب")
    else
        listOf("البيانات", "الأسنان", "الصور")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(patient?.fullName ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "رجوع") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(title) })
                }
            }

            when (tabIndex) {
                0 -> patient?.let { PatientInfoTab(it) }
                1 -> ToothChartTab(repository, patientId)
                2 -> PatientImagesTab(repository, patientId)
                3 -> if (Session.isAdmin) PatientFinanceTab(repository, patientId, patient?.fullName ?: "")
            }
        }
    }
}

@Composable
fun PatientInfoTab(patient: Patient) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        InfoRow("رقم الملف", patient.fileNumber)
        InfoRow("الاسم الكامل", patient.fullName)
        InfoRow("تاريخ الميلاد", patient.birthDate)
        InfoRow("رقم الهاتف", patient.phone)
        InfoRow("ملاحظات", patient.notes)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
        Text(value.ifBlank { "-" }, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
    Divider()
}

@Composable
fun ToothChartTab(repository: ClinicRepository, patientId: Long) {
    val scope = rememberCoroutineScope()
    var selectedTooth by remember { mutableStateOf<Int?>(null) }
    val records by repository.toothDao.getByPatient(patientId).collectAsState(initial = emptyList())
    val statusMap = remember(records) { records.associateBy { it.toothNumber } }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("اضغط على أي سن لتعديل حالته", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(8), modifier = Modifier.weight(1f)) {
            items((1..32).toList()) { toothNum ->
                val record = statusMap[toothNum]
                val hasIssue = record != null && record.status != "سليم"
                Card(
                    modifier = Modifier.padding(4.dp).aspectRatio(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (hasIssue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = { selectedTooth = toothNum }
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text(toothNum.toString(), fontSize = 12.sp)
                    }
                }
            }
        }
    }

    selectedTooth?.let { toothNum ->
        val existing = statusMap[toothNum]
        ToothEditDialog(
            toothNumber = toothNum,
            existing = existing,
            onDismiss = { selectedTooth = null },
            onSave = { status, rootCanal, notes ->
                scope.launch {
                    repository.toothDao.upsert(
                        ToothRecord(
                            id = existing?.id ?: 0,
                            patientId = patientId,
                            toothNumber = toothNum,
                            status = status,
                            rootCanal = rootCanal,
                            notes = notes
                        )
                    )
                    selectedTooth = null
                }
            }
        )
    }
}

@Composable
fun ToothEditDialog(
    toothNumber: Int,
    existing: ToothRecord?,
    onDismiss: () -> Unit,
    onSave: (String, Boolean, String) -> Unit
) {
    var status by remember { mutableStateOf(existing?.status ?: "سليم") }
    var rootCanal by remember { mutableStateOf(existing?.rootCanal ?: false) }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    val options = listOf("سليم", "تسوس", "محشو", "مقلوع", "تاج", "يحتاج علاج عصب")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("السن رقم $toothNumber") },
        text = {
            Column {
                Text("الحالة:", fontSize = 13.sp)
                options.forEach { opt ->
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(selected = status == opt, onClick = { status = opt })
                        Text(opt)
                    }
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = rootCanal, onCheckedChange = { rootCanal = it })
                    Text("علاج قناة جذر")
                }
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("ملاحظات") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onSave(status, rootCanal, notes) }) { Text("حفظ") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
fun PatientImagesTab(repository: ClinicRepository, patientId: Long) {
    val scope = rememberCoroutineScope()
    val images by repository.imageDao.getByPatient(patientId).collectAsState(initial = emptyList())
    var pendingUri by remember { mutableStateOf<String?>(null) }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) pendingUri = uri.toString()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("إضافة صورة")
        }
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f)) {
            items(images) { img ->
                Column(Modifier.padding(4.dp)) {
                    AsyncImage(
                        model = img.uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    )
                    if (img.note.isNotBlank()) Text(img.note, fontSize = 11.sp)
                }
            }
        }
    }

    pendingUri?.let { uriStr ->
        var note by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { pendingUri = null },
            title = { Text("ملاحظة على الصورة") },
            text = {
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("ملاحظة (اختياري)") }, modifier = Modifier.fillMaxWidth())
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        repository.imageDao.insert(
                            PatientImage(patientId = patientId, uri = uriStr, note = note, date = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date()))
                        )
                        pendingUri = null
                    }
                }) { Text("حفظ") }
            },
            dismissButton = { TextButton(onClick = { pendingUri = null }) { Text("إلغاء") } }
        )
    }
}

@Composable
fun PatientFinanceTab(repository: ClinicRepository, patientId: Long, patientName: String) {
    val scope = rememberCoroutineScope()
    var showAdd by remember { mutableStateOf(false) }
    val payments by repository.paymentDao.getByPatient(patientId).collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, contentDescription = "إضافة حركة") }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(payments) { p ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${p.type} - ${p.amount} ${p.currency}", fontWeight = FontWeight.Bold)
                        Text(p.date, fontSize = 12.sp)
                        if (p.note.isNotBlank()) Text(p.note, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddPaymentDialog(
            onDismiss = { showAdd = false },
            onSave = { amount, currency, type, note ->
                scope.launch {
                    repository.paymentDao.insert(
                        Payment(
                            patientId = patientId,
                            patientName = patientName,
                            amount = amount,
                            currency = currency,
                            type = type,
                            date = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date()),
                            note = note
                        )
                    )
                    showAdd = false
                }
            }
        )
    }
}

@Composable
fun AddPaymentDialog(onDismiss: () -> Unit, onSave: (Double, String, String, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("SYP") }
    var type by remember { mutableStateOf("مستحق") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة حركة مالية") },
        text = {
            Column {
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("المبلغ") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Row {
                    FilterChip(selected = currency == "SYP", onClick = { currency = "SYP" }, label = { Text("ليرة سورية") })
                    Spacer(Modifier.width(8.dp))
                    FilterChip(selected = currency == "USD", onClick = { currency = "USD" }, label = { Text("دولار") })
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    FilterChip(selected = type == "مستحق", onClick = { type = "مستحق" }, label = { Text("مستحق") })
                    Spacer(Modifier.width(8.dp))
                    FilterChip(selected = type == "دفعة", onClick = { type = "دفعة" }, label = { Text("دفعة") })
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("ملاحظة") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.toDoubleOrNull() ?: 0.0
                if (amt > 0) onSave(amt, currency, type, note)
            }) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
