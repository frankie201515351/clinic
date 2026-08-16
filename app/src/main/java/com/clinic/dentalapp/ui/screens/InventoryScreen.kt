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
import com.clinic.dentalapp.data.entity.InventoryItem
import kotlinx.coroutines.launch

@Composable
fun InventoryScreen(repository: ClinicRepository) {
    val scope = rememberCoroutineScope()
    var showAdd by remember { mutableStateOf(false) }
    val items by repository.inventoryDao.getAll().collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, contentDescription = "إضافة صنف") }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("المخزون", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            LazyColumn {
                items(items) { item ->
                    val low = item.quantity <= item.minQuantity
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (low) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold)
                                Text("${item.quantity} ${item.unit}" + if (low) " — الكمية منخفضة!" else "", fontSize = 13.sp)
                            }
                            IconButton(onClick = { scope.launch { repository.inventoryDao.delete(item) } }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddInventoryDialog(
            onDismiss = { showAdd = false },
            onSave = { name, qty, minQty, unit ->
                scope.launch {
                    repository.inventoryDao.insert(InventoryItem(name = name, quantity = qty, minQuantity = minQty, unit = unit))
                    showAdd = false
                }
            }
        )
    }
}

@Composable
fun AddInventoryDialog(onDismiss: () -> Unit, onSave: (String, Int, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var minQty by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("قطعة") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة صنف") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم الصنف") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("الكمية الحالية") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = minQty, onValueChange = { minQty = it }, label = { Text("حد التنبيه الأدنى") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("الوحدة") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onSave(name, qty.toIntOrNull() ?: 0, minQty.toIntOrNull() ?: 0, unit)
            }) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
